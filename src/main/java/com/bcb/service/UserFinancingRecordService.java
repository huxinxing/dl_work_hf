package com.bcb.service;

import com.bcb.config.RedisClient;
import com.bcb.domain.repository.*;
import com.bcb.domain.entity.*;
import com.bcb.domain.enums.UserFinancingRecordStatusEnum;
import com.bcb.domain.enums.UserTransferTypeEnum;
import com.bcb.util.CommonUtil;
import com.bcb.util.RandomUtil;
import com.bcb.util.annotation.TransactionEx;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
@Service
public class UserFinancingRecordService {

    @Autowired
    private FinancingDailyRateRepository financingDailyRateRepository;

    @Autowired
    private CoinRepository coinRepository;

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private SystemConfigurationRepository systemConfigurationRepository;

    @Autowired
    private UserAccountInfoRepository userAccountInfoRepository;

    @Autowired
    private UserFinancingSettlementRepository userFinancingSettlementRepository;

    @Autowired
    UserFinancingRecordRepository userFinancingRecordRepository;

    @Autowired
    private UserTransferRepository userTransferRepository;

    @Autowired
    FinancingBaseInfoRepository financingBaseInfoRepository;

    @Autowired
    RedisClient redisClient;

    @Autowired
    UserAgentsFinancingBillRepository userAgentsFinancingBillRepository;

    @Autowired
    UserInterstCouponsRelationRepository userInterstCouponsRelationRepository;

    @Autowired
    SystemCoinRepository systemCoinRepository;

    @Autowired
    UserWalletInfoRepository userWalletInfoRepository;

    @Autowired
    FinancingActivityInterstCouponsRepository financingActivityInterstCouponsRepository;



    private static final String SETTLEMENT_PREFIX = "settle";
    private static final String FOUNDATION_USERID = "FoundationUserId";

    /**
     * 获取年化率和实际比例的比例：理财期限6个月，实际比例是0.5
     *
     * @return
     */
    private BigDecimal getActualRatio(String financingUuid) throws Exception {
        FinancingBaseInfo info = financingBaseInfoRepository.findOneByFinancingUuid(financingUuid);
        if (info.getFreezeUnit().equals("年")) {
            return BigDecimal.valueOf(info.getFreezeNumber());
        } else if (info.getFreezeUnit().equals("月")) {
            return BigDecimal.valueOf(info.getFreezeNumber()).divide(new BigDecimal("12"));
        } else if (info.getFreezeUnit().equals("日")) {
            return BigDecimal.valueOf(info.getFreezeNumber()).divide(new BigDecimal("365"));
        } else if (info.getFreezeUnit().equals("周")) {
            return BigDecimal.valueOf(info.getFreezeNumber()).divide(new BigDecimal("52"));
        } else {
            throw new Exception("不支持的时间周期：" + info.getFreezeUnit());
        }
    }

    /**
     * 给一条投资记录进行结算，按期结算
     *
     * @param record
     */
    @TransactionEx
    public void settle(UserFinancingRecord record) throws Exception {
        if (record.getRedeemptionStatus() == 1 || record.getRedeemptionStatus() == 3) {
            log.warn("该条记录已本金赎回！");
            record.setRecordStatus(UserFinancingRecord.RECORD_STATUS_SETTLEMENT_OVER);
            userFinancingRecordRepository.save(record);
            return;
        }

        if (record.getRecordStatus() != UserFinancingRecordStatusEnum.理财结束.getValue()) {
            log.warn("不是结束状态理财，不予结算");
            return;
        }
        // 获取币种编号
        Coin coin = coinRepository.findByName("BCB");
        // 获取系统的用户账户
        SystemConfiguration config = systemConfigurationRepository.findByName(FOUNDATION_USERID);
        // 基金公司的账户余额信息
        Integer foundationUserId = Integer.valueOf(config.getValue());
        UserBalance foundationBalance = userBalanceRepository.findByUserIdAndCoinId(foundationUserId, coin.getId());
        if (foundationBalance == null) {
            // 没有余额记录，则生成余额记录
            foundationBalance = new UserBalance();
            foundationBalance.setBalance(BigDecimal.ZERO);
            foundationBalance.setAddress(null);
            foundationBalance.setCoinId(coin.getId());
            foundationBalance.setUserId(foundationUserId);
            foundationBalance.setFrozen(BigDecimal.ZERO);
        }
        UserAccountInfo info = userAccountInfoRepository.findByUserId(record.getUserId());
        if (ObjectUtils.isEmpty(info)) {
            throw new Exception("用户不存在");
        }

        // 获取最终收益率，按结算日期计算汇率
        UserFinancingSettlement settlement = new UserFinancingSettlement();
        Timestamp date = record.getExpireTime();
        FinancingDailyRate dailyRate = financingDailyRateRepository.findByCreateTime(date);
        if (ObjectUtils.isEmpty(dailyRate)) {
            List<FinancingDailyRate> list = financingDailyRateRepository.findByOrderByCreateTimeDesc();
            for (int i = 0; i < list.size(); i++) {
                if (StringUtils.isEmpty(list.get(i).getBcb2Usdx()))
                    continue;
                settlement.setBcb2usdx(list.get(i).getBcb2Usdx());
                break;
            }
            if (StringUtils.isEmpty(settlement.getBcb2usdx()))
                settlement.setBcb2usdx(new BigDecimal("0.00"));
        } else {
            settlement.setBcb2usdx(dailyRate.getBcb2Usdx());
        }
        // 生成结算记录
        settlement.setId(RandomUtil.getUuid(SETTLEMENT_PREFIX));
        settlement.setUserFinancingRecordId(record.getId());

        // 按照年化服务费，计算最终服务费
        settlement.setServiceRate(record.getServiceRate().multiply(getActualRatio(record.getFinancingUuid())));
        settlement.setServiceAmount(record.getUsdxAmount().multiply(settlement.getServiceRate()).divide(settlement.getBcb2usdx(), 9, BigDecimal.ROUND_HALF_UP));
        // 计算固定收益率：(年华收益率 - 服务费率) *  年

        settlement.setFixedRate((record.getAnnualRate().subtract(record.getServiceRate())).multiply(getActualRatio(record.getFinancingUuid())));

        // 最终收益率
        BigDecimal totalRate = settlement.getBcb2usdx().divide(record.getBcb2usdx(), 9, BigDecimal.ROUND_HALF_UP);

        settlement.setModifyTime(new Timestamp(System.currentTimeMillis()));
        settlement.setRemark("按期结算");
        settlement.setSettlementDate(new Date(System.currentTimeMillis()));

        // 固定收益部分
        if (record.getRedeemptionStatus() == 2) {
            settlement.setFixedUsdxAmount(BigDecimal.ZERO);
            settlement.setFixedBcbAmount(BigDecimal.ZERO);
        } else {
            settlement.setFixedUsdxAmount(record.getUsdxAmount().multiply(settlement.getFixedRate()));
            settlement.setFixedBcbAmount(settlement.getFixedUsdxAmount().divide(settlement.getBcb2usdx(), 9, BigDecimal.ROUND_HALF_UP));
        }

        settlement.setPrincipalBcbAmount(record.getUsdxAmount().divide(settlement.getBcb2usdx(),9,BigDecimal.ROUND_HALF_DOWN));
        settlement.setPrincipalUsdxAmount(record.getUsdxAmount());
        settlement.setPrincipalRate(new BigDecimal(1.0));

        // 获取本用户的账户余额信息
        UserBalance userBalance = userBalanceRepository.findByUserIdAndCoinId(record.getUserId(), coin.getId());
        if (userBalance == null) {
            // 没有余额记录，则生成余额记录
            userBalance = new UserBalance();
            userBalance.setBalance(BigDecimal.ZERO);
            userBalance.setAddress(null);
            userBalance.setCoinId(coin.getId());
            userBalance.setUserId(record.getUserId());
            userBalance.setFrozen(BigDecimal.ZERO);
        }

        // 需要获取上级的用户id
        UserBalance superiorBalance = userBalanceRepository.findByUserIdAndCoinId(info.getParentId(), coin.getId());
        if (superiorBalance == null) {
            // 没有余额记录，则生成余额记录
            superiorBalance = new UserBalance();
            superiorBalance.setBalance(BigDecimal.ZERO);
            superiorBalance.setAddress(null);
            superiorBalance.setCoinId(coin.getId());
            superiorBalance.setUserId(info.getParentId());
            superiorBalance.setFrozen(BigDecimal.ZERO);
        }

        // 生成流水记录
        // 固定收益
        userBalance.setBalance(userBalance.getBalance().add(settlement.getFixedBcbAmount()).add(settlement.getPrincipalBcbAmount()));
        //userBalance.setModifyTime(new Timestamp(System.currentTimeMillis()));
        UserTransfer fixedTransfer = new UserTransfer();
        fixedTransfer.setAmount(settlement.getFixedBcbAmount().add(settlement.getPrincipalBcbAmount()));
        fixedTransfer.setUserId(record.getUserId());
        fixedTransfer.setCoinId(coin.getId());
        fixedTransfer.setFromAddress(record.getToAddress());
        fixedTransfer.setToAddress(null);
        fixedTransfer.setFee(BigDecimal.ZERO);
        fixedTransfer.setBalance(userBalance.getBalance());
        fixedTransfer.setModifyTime(new Timestamp(System.currentTimeMillis()));
        fixedTransfer.setType(5);
        fixedTransfer.setRemark("投资（" + record.getId() + "）理财项目（" + record.getFinancingUuid() + "）的固定收益和本金");
        // 保存结果
        userTransferRepository.save(fixedTransfer);
        //userBalanceRepository.save(userBalance); // 留在最后记录

        // 根据最终收益率计算额外收益
        if (totalRate.compareTo(settlement.getFixedRate().add(new BigDecimal("1")).add(settlement.getServiceRate())) > 0) {
            // 最终收益率 > 固定收益率 + 服务费
            //额外收益总额
            BigDecimal extraRate = totalRate.subtract(new BigDecimal("1.0").add(settlement.getFixedRate()).add(settlement.getServiceRate()));
            settlement.setExtraUsdxAmount(extraRate.multiply(record.getUsdxAmount()));
            settlement.setExtraBcbAmount(settlement.getExtraUsdxAmount().divide(settlement.getBcb2usdx(), 9, BigDecimal.ROUND_HALF_UP));

            // 分佣
            // 基金公司分佣 = 额外收益总额×基金公司分佣比例
            settlement.setFoundationCommission(settlement.getExtraBcbAmount().multiply(record.getFoundationCommissionRate()));

            // 生成相应的流水记录

            // 基金公司的额外收益
            foundationBalance.setBalance(foundationBalance.getBalance().add(settlement.getFoundationCommission()));
            UserTransfer foundationTransfer = new UserTransfer();
            foundationTransfer.setAmount(settlement.getFoundationCommission());
            foundationTransfer.setUserId(Integer.valueOf(config.getValue()));
            foundationTransfer.setCoinId(coin.getId());
            foundationTransfer.setFromAddress(record.getToAddress());
            foundationTransfer.setToAddress(null);
            foundationTransfer.setFee(BigDecimal.ZERO);
            foundationTransfer.setBalance(foundationBalance.getBalance());
            foundationTransfer.setModifyTime(new Timestamp(System.currentTimeMillis()));
            foundationTransfer.setType(8);
            foundationTransfer.setRemark("投资（" + record.getId() + "）理财项目（" + record.getFinancingUuid() + "）的基金公司分拥");

            // 基金公司服务费收益
            foundationBalance.setBalance(foundationBalance.getBalance().add(settlement.getServiceAmount()));
            UserTransfer foundationServiceTransfer = new UserTransfer();
            foundationServiceTransfer.setAmount(settlement.getServiceAmount());
            foundationServiceTransfer.setUserId(Integer.valueOf(config.getValue()));
            foundationServiceTransfer.setCoinId(coin.getId());
            foundationServiceTransfer.setFromAddress(record.getToAddress());
            foundationServiceTransfer.setToAddress(null);
            foundationServiceTransfer.setFee(BigDecimal.ZERO);
            foundationServiceTransfer.setBalance(foundationBalance.getBalance());
            foundationServiceTransfer.setModifyTime(new Timestamp(System.currentTimeMillis()));
            foundationServiceTransfer.setType(14);
            foundationServiceTransfer.setRemark("投资（" + record.getId() + "）理财项目（" + record.getFinancingUuid() + "）的服务费收益");
            // 保存结果
            userTransferRepository.save(foundationServiceTransfer);
            userTransferRepository.save(foundationTransfer);
            userBalanceRepository.save(foundationBalance);

            // 上级分佣总额：含实际上级分佣和税费
            BigDecimal superiorTotal = BigDecimal.ZERO;
            if (info.getParentId() == null) {
                // 没有上级，不需要上级分佣
                superiorTotal = BigDecimal.ZERO;
                settlement.setSuperiorCommission(BigDecimal.ZERO);
                settlement.setSuperiorCommissionTax(BigDecimal.ZERO);
            } else {
                // 有上级，则需要分佣
                SystemConfiguration rateConfig = systemConfigurationRepository.findByName(SystemConfiguration.TAX_RATE);

                // 上级分佣总额 = （额外收益总额）×上级分佣比例
                // 税 = 上级分佣总额×系统税率
                // 上级分佣 = 上级分佣总额×税
                superiorTotal = settlement.getExtraBcbAmount().multiply(record.getSuperiorCommissionRate());
                BigDecimal fee = superiorTotal.multiply(new BigDecimal(rateConfig.getValue()));
                settlement.setSuperiorCommission(superiorTotal.subtract(fee));
                settlement.setSuperiorCommissionTax(fee);

                // 上级的额外收益：
                superiorBalance.setBalance(superiorBalance.getBalance().add(settlement.getSuperiorCommission()));
                UserTransfer superiorTransfer = new UserTransfer();
                superiorTransfer.setAmount(settlement.getSuperiorCommission());
                superiorTransfer.setUserId(record.getUserId());
                superiorTransfer.setCoinId(coin.getId());
                superiorTransfer.setFromAddress(record.getToAddress());
                superiorTransfer.setToAddress(null);
                superiorTransfer.setFee(fee);
                superiorTransfer.setBalance(superiorBalance.getBalance());
                superiorTransfer.setModifyTime(new Timestamp(System.currentTimeMillis()));
                superiorTransfer.setType(7);
                superiorTransfer.setRemark("投资（" + record.getId() + "）理财项目（" + record.getFinancingUuid() + "）的资金担保机构分拥分佣");

                // 保存结果

                userTransferRepository.save(superiorTransfer);
                userBalanceRepository.save(superiorBalance);
            }

            // 本人的额外收益 = 额外收益总额 - 基金公司分佣 - 上级分佣总额
            BigDecimal extraBcb = settlement.getExtraBcbAmount().subtract(settlement.getFoundationCommission()).subtract(superiorTotal);
            userBalance.setBalance(userBalance.getBalance().add(extraBcb));
            UserTransfer extraTransfer = new UserTransfer();
            extraTransfer.setAmount(extraBcb);
            extraTransfer.setUserId(record.getUserId());
            extraTransfer.setCoinId(coin.getId());
            extraTransfer.setFromAddress(record.getToAddress());
            extraTransfer.setToAddress(null);
            extraTransfer.setFee(BigDecimal.ZERO);
            extraTransfer.setBalance(userBalance.getBalance());
            extraTransfer.setModifyTime(new Timestamp(System.currentTimeMillis()));
            extraTransfer.setType(6);
            extraTransfer.setRemark("投资（" + record.getId() + "）理财项目（" + record.getFinancingUuid() + "）的额外收益本人分拥收益");
            // 保存结果
            userTransferRepository.save(extraTransfer);
        } else {
            // 最终收益率不足，没有额外收益
            settlement.setExtraUsdxAmount(BigDecimal.ZERO);
            settlement.setExtraBcbAmount(BigDecimal.ZERO);

            // 分佣
            settlement.setFoundationCommission(BigDecimal.ZERO);
            settlement.setSuperiorCommission(BigDecimal.ZERO);
            settlement.setSuperiorCommissionTax(BigDecimal.ZERO);

            // 计算因总收益率不足，造成基金公司损失：损失服务费或更多的资金
            BigDecimal leftRate = totalRate.subtract(settlement.getFixedRate());
            // 剩余的收益率都是基金公司所得，可能大于0，也可能小于0
            //        BigDecimal s = record.getUsdxAmount().multiply(settlement.getFixedRate().subtract(totalRate).add(BigDecimal.valueOf(1))).divide(settlement.getBcb2usdx(),9,BigDecimal.ROUND_HALF_DOWN);
            foundationBalance.setBalance(foundationBalance.getBalance().subtract(record.getUsdxAmount().multiply(settlement.getFixedRate().subtract(totalRate).add(BigDecimal.valueOf(1))).divide(settlement.getBcb2usdx(), 9, BigDecimal.ROUND_HALF_DOWN)));
            //           foundationBalance.setBalance(foundationBalance.getBalance().add(record.getUsdxAmount().multiply(leftRate).divide(settlement.getBcb2usdx(), 9, BigDecimal.ROUND_HALF_UP)));
            UserTransfer foundationTransfer = new UserTransfer();
            foundationTransfer.setAmount(record.getUsdxAmount().multiply(leftRate).multiply(settlement.getBcb2usdx()));
            foundationTransfer.setUserId(Integer.valueOf(config.getValue()));
            foundationTransfer.setCoinId(coin.getId());
            foundationTransfer.setFromAddress(record.getToAddress());
            foundationTransfer.setToAddress(null);
            foundationTransfer.setFee(BigDecimal.ZERO);
            foundationTransfer.setBalance(foundationBalance.getBalance());
            foundationTransfer.setModifyTime(new Timestamp(System.currentTimeMillis()));
            foundationTransfer.setType(8);
            foundationTransfer.setRemark("投资（" + record.getId() + "）理财项目（" + record.getFinancingUuid() + "）中，分配用户固定收益后，基金盈亏(含服务费："
                    + settlement.getServiceAmount().stripTrailingZeros().toPlainString() + ")：" + foundationTransfer.getAmount().stripTrailingZeros().toPlainString());

            userTransferRepository.save(foundationTransfer);
            userBalanceRepository.save(foundationBalance);
        }

        //加息券进余额,对加息券进行结算
        UserInterstCouponsRelation userInterstCouponsRelation = userInterstCouponsRelationRepository.findByRecordId(record.getId());

        if (!ObjectUtils.isEmpty(userInterstCouponsRelation)) {

            if(userInterstCouponsRelation.getIcStatus() == 1){  //计息中的订单
                FinancingActivityInterstCoupons financingActivityInterstCoupons = financingActivityInterstCouponsRepository.findByIcId(userInterstCouponsRelation.getIcId());
                if (ObjectUtils.isEmpty(financingActivityInterstCoupons)) {
                    log.error("加息券项目不存在，无法对订单进行结算: " + userInterstCouponsRelation);
                    return;
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                UserFinancingRecord userFinancingRecord = userFinancingRecordRepository.findOneById(userInterstCouponsRelation.getRecordId());
                Integer dayNum = CommonUtil.surplusDay(simpleDateFormat.format(userInterstCouponsRelation.getIcBeginTime()),simpleDateFormat.format(userFinancingRecord.getExpireTime()));
                BigDecimal amountUSDX = BigDecimal.ZERO;
                amountUSDX = amountUSDX.add(userFinancingRecord.getUsdxAmount().multiply(financingActivityInterstCoupons.getIcRate()).multiply(BigDecimal.valueOf(dayNum)).divide(BigDecimal.valueOf(365), 9, BigDecimal.ROUND_HALF_DOWN));
                userInterstCouponsRelation.setIcStatus(2);
                userInterstCouponsRelation.setIcSeletment(amountUSDX);
                userInterstCouponsRelation.setCoinName("USDX");
                userInterstCouponsRelation.setCoinId(systemCoinRepository.findByName("USDX_BCB").getId());
                userInterstCouponsRelation.setIcEndTime(record.getExpireTime());
                userInterstCouponsRelationRepository.save(userInterstCouponsRelation);
            }

            userInterstCouponsRelation.setBcb2Usdx(settlement.getBcb2usdx());

            List<UserWalletInfo> userWalletInfoList = userWalletInfoRepository.findByUserId(userInterstCouponsRelation.getUserId());
            UserTransfer ICTransfer = new UserTransfer();
            ICTransfer.setAmount(userInterstCouponsRelation.getIcSeletment().divide(settlement.getBcb2usdx(),9,BigDecimal.ROUND_HALF_DOWN));
            ICTransfer.setUserId(userInterstCouponsRelation.getUserId());
            ICTransfer.setCoinId(systemCoinRepository.findByName("BCB").getId());
            ICTransfer.setFromAddress(null);
            ICTransfer.setToAddress(ObjectUtils.isEmpty(userWalletInfoList.get(0).getToken()) ? "" : userWalletInfoList.get(0).getToken());
            ICTransfer.setFee(BigDecimal.ZERO);
            ICTransfer.setBalance(userBalance.getBalance());
            ICTransfer.setType(12);
            ICTransfer.setRemark("加息券投资（" + record.getFinancingUuid() + "）理财项目的利息");
            userBalance.setBalance(userBalance.getBalance().add(userInterstCouponsRelation.getIcSeletment().divide(settlement.getBcb2usdx(),9,BigDecimal.ROUND_HALF_DOWN)));

            userInterstCouponsRelationRepository.save(userInterstCouponsRelation);
            userTransferRepository.save(ICTransfer);
        }

        record.setRecordStatus(UserFinancingRecord.RECORD_STATUS_SETTLEMENT_OVER);
        userFinancingRecordRepository.save(record);
        // 保存结算记录

        userFinancingSettlementRepository.save(settlement);
        userBalanceRepository.save(userBalance);
    }

    /**
     * 更新投资记录状态
     *
     * @param record
     * @param status
     */
    @TransactionEx
    public void updateStatus(UserFinancingRecord record, Integer status) {
        record.setRecordStatus(status);
        userFinancingRecordRepository.save(record);
    }

    /**
     * 给投资记录结算代理收益
     *
     * @param record
     */
    @TransactionEx
    public void settleAgentsRebate(UserFinancingRecord record) throws Exception {
        // 检查投资记录状态，只有进行中且未计算代理收益的才需要计算
        if (record.getRecordStatus() != UserFinancingRecord.RECORD_STATUS_ONGOING || record.getAgentsStatus() != UserFinancingRecord.AGENTS_STATUS_FAIL) {
            log.info("订单不是进行的订单或代理收益已结算" + record.toString());
            return;
        }
        Coin coin = coinRepository.findByName("BCB");
        SystemConfiguration config = systemConfigurationRepository.findByName(SystemConfiguration.TAX_RATE);
        BigDecimal systemRate = new BigDecimal(config.getValue());
        BigDecimal lastUserScale = BigDecimal.ZERO;
        Integer userId = record.getUserId();
        while (userId != null) {
            // 用户存在，则计算代理收益
            UserAccountInfo info = userAccountInfoRepository.findByUserId(userId);
            BigDecimal rate = info.getFinancingScale().subtract(lastUserScale);
            if (ObjectUtils.isEmpty(info)) {
                throw new Exception("用户信息不存在: userId=" + userId);
            }
            // 本用户该得的收益比例

            if (rate.compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("用户(" + info.getUserId() + ")" + info.getDisplayName() + "的代理返点不能小于0，不合理");
            }

            BigDecimal amount = rate.multiply(record.getBcbAmount()).divide(BigDecimal.valueOf(100), 9, BigDecimal.ROUND_HALF_DOWN);
            BigDecimal fee = amount.multiply(systemRate);
            BigDecimal rebate = amount.subtract(fee);

            // 记录用户余额
            UserBalance balance = userBalanceRepository.findByUserIdAndCoinId(userId, coin.getId());
            if (balance == null) {
                // 没有余额记录，则生成余额记录
                balance = new UserBalance();
                balance.setBalance(BigDecimal.ZERO);
                balance.setAddress(null);
                balance.setCoinId(coin.getId());
                balance.setUserId(info.getUserId());
                balance.setFrozen(BigDecimal.ZERO);
            }
            balance.setBalance(balance.getBalance().add(rebate));
            // 记录用户流水
            UserTransfer transfer = new UserTransfer();
            transfer.setUserId(userId);
            transfer.setRecordId(record.getId());
            transfer.setCoinId(coin.getId());
            transfer.setFromAddress(record.getToAddress());
            transfer.setToAddress(null);
            transfer.setFee(fee);
            transfer.setAmount(amount);
            transfer.setBalance(balance.getBalance());
            transfer.setModifyTime(new Timestamp(System.currentTimeMillis()));
            transfer.setType(4);
            transfer.setRemark("投资（" + record.getId() + "）理财项目（" + record.getFinancingUuid() + "）的代理收益");

            UserAgentsFinancingBill userAgentsFinancingBill = new UserAgentsFinancingBill();
            userAgentsFinancingBill.setAmount(rebate);
            userAgentsFinancingBill.setCreateTime(new java.util.Date());
            userAgentsFinancingBill.setRecordId(record.getId());
            userAgentsFinancingBill.setUserId(userId);
            userAgentsFinancingBill.setTax(fee);

            userAgentsFinancingBillRepository.save(userAgentsFinancingBill);
            userTransferRepository.save(transfer);
            userBalanceRepository.save(balance);

            // 循环处理上级
            lastUserScale = info.getFinancingScale();
            userId = info.getParentId();
        }
        record.setAgentsStatus(UserFinancingRecord.AGENTS_STATUS_SUCCESS);   //设置订单代理返点状态
        userFinancingRecordRepository.save(record);

    }

}
