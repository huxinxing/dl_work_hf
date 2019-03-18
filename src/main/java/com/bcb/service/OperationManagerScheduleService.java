package com.bcb.service;

import com.bcb.config.RedisClient;
import com.bcb.domain.entity.*;
import com.bcb.domain.enums.UserTransferTypeEnum;
import com.bcb.domain.repository.*;
import com.bcb.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OperationManagerScheduleService {

    @Autowired
    UserGoldexperienceRelationRepository userGoldexperienceRelationRepository;

    @Autowired
    FinancingActivityGoldexperienceRepository financingActivityGoldexperienceRepository;

    @Autowired
    UserBalanceRepository userBalanceRepository;

    @Autowired
    UserTransferRepository userTransferRepository;

    @Autowired
    UserWalletInfoRepository userWalletInfoRepository;

    @Autowired
    RedisClient redisClient;

    @Autowired
    SystemCoinRepository systemCoinRepository;

    @Autowired
    UserInterstCouponsRelationRepository userInterstCouponsRelationRepository;

    @Autowired
    FinancingActivityInterstCouponsRepository financingActivityInterstCouponsRepository;

    @Autowired
    UserFinancingRecordRepository userFinancingRecordRepository;

    @Transactional
    public void goldExperienceFailCheck(UserGoldexperienceRelation goldexperience) throws Exception {

        FinancingActivityGoldexperience financingActivityGoldexperience = financingActivityGoldexperienceRepository.findByGeId(goldexperience.getGeId());
        if (ObjectUtils.isEmpty(financingActivityGoldexperience)) {
            goldexperience.setExperienceStatus(3);
            log.info("体验金项目不存在");
        } else {
            if (StringUtils.isEmpty(financingActivityGoldexperience.getValidityDay())) {
                if ((financingActivityGoldexperience.getValidityEndTime().getTime() - new Date().getTime()) < 0) {
                    goldexperience.setExperienceStatus(3);
                    log.info("体验金已过期");
                }
            } else {
                if (StrTime(goldexperience.getReceiveDate(), financingActivityGoldexperience.getValidityDay()).getTime() - new Date().getTime() < 0) {
                    goldexperience.setExperienceStatus(3);
                    log.info("体验金已过期");
                }
            }
        }
        if (goldexperience.getExperienceStatus() == 3) {
            userGoldexperienceRelationRepository.save(goldexperience);
        }
    }

    @Transactional
    public void goldExperienceCalculate(UserGoldexperienceRelation userGoldexperienceRelation) throws Exception {
        //体验金返回都是BCB，其中结算进流水，到余额。本金进基金公司余额，利息进用户余额，利息计算方式为【(年化率 - 服务费) *  时长/365】
        FinancingActivityGoldexperience financingActivityGoldexperience = financingActivityGoldexperienceRepository.findByGeId(userGoldexperienceRelation.getGeId());
        if (ObjectUtils.isEmpty(financingActivityGoldexperience)) {
            log.error("体验金项目不存在 ，无法对订单进行结算:" + userGoldexperienceRelation);
            return;
        }

        BigDecimal amountBCB = (userGoldexperienceRelation.getAnnualRate().subtract(userGoldexperienceRelation.getServiceRate())).multiply(BigDecimal.valueOf(CommonUtil.surplusDay(userGoldexperienceRelation.getExperienceBeginTime(), userGoldexperienceRelation.getExperienceEndTime()))).divide(BigDecimal.valueOf(365), 9, BigDecimal.ROUND_HALF_UP).multiply(financingActivityGoldexperience.getGeAmount()).divide(new BigDecimal(redisClient.get("BCB_USDX")), 9, BigDecimal.ROUND_HALF_DOWN);

        //判断用户是否拥有钱包地址
        List<UserWalletInfo> userWalletInfoList = userWalletInfoRepository.findByUserId(userGoldexperienceRelation.getUserId());


        //利息进用户余额
        UserBalance userBalance = userBalanceRepository.findByUserIdAndCoinId(userGoldexperienceRelation.getUserId(), 1);
        if (ObjectUtils.isEmpty(userBalance)) {
            // 没有余额记录，则生成余额记录
            userBalance = new UserBalance();
            userBalance.setBalance(BigDecimal.ZERO);
            userBalance.setAddress(null);
            userBalance.setCoinId(userGoldexperienceRelation.getId());
            userBalance.setUserId(userGoldexperienceRelation.getUserId());
            userBalance.setFrozen(BigDecimal.ZERO);
        }
        userBalance.setBalance(amountBCB.add(userBalance.getBalance()));

        UserTransfer goldExperienceTransfer = new UserTransfer();
        goldExperienceTransfer.setAmount(amountBCB);
        goldExperienceTransfer.setUserId(userGoldexperienceRelation.getUserId());
        goldExperienceTransfer.setCoinId(systemCoinRepository.findByName("BCB").getId());
        goldExperienceTransfer.setFromAddress(null);
        goldExperienceTransfer.setToAddress(ObjectUtils.isEmpty(userWalletInfoList.get(0).getToken())?"":userWalletInfoList.get(0).getToken());
        goldExperienceTransfer.setFee(BigDecimal.ZERO);
        goldExperienceTransfer.setBalance(userBalance.getBalance());
        goldExperienceTransfer.setModifyTime(new Timestamp(System.currentTimeMillis()));
        goldExperienceTransfer.setType(11);
        goldExperienceTransfer.setRemark("体验金投资（" + userGoldexperienceRelation.getFinancingUuid() + "）理财项目的收益");
        System.out.println(userBalance.toString());
        userBalanceRepository.save(userBalance);
        userTransferRepository.save(goldExperienceTransfer);

        userGoldexperienceRelation.setBcb2Usdx(new BigDecimal(redisClient.get("BCB_USDX")));
        userGoldexperienceRelation.setExperienceStatus(2);
        userGoldexperienceRelation.setGeAmount(amountBCB);
        userGoldexperienceRelation.setCoinName("BCB");
        userGoldexperienceRelation.setCoinId(systemCoinRepository.findByName("BCB").getId());
        userGoldexperienceRelationRepository.save(userGoldexperienceRelation);

    }

    @Transactional
    public void interstCouponsFailCheck(UserInterstCouponsRelation interstCoupons) throws ParseException {
        FinancingActivityInterstCoupons financingActivityInterstCoupons = financingActivityInterstCouponsRepository.findByIcId(interstCoupons.getIcId());
        if (ObjectUtils.isEmpty(financingActivityInterstCoupons)) {
            interstCoupons.setIcStatus(3);
            log.info("加息券项目不存在");

        } else {
            if (StringUtils.isEmpty(financingActivityInterstCoupons.getIcValidityDay())) {
                if ((financingActivityInterstCoupons.getIcValidityEndTime().getTime() - new Date().getTime()) < 0) {
                    interstCoupons.setIcStatus(3);
                    log.info("加息券订单已过期");
                }
            } else {
                if (StrTime(interstCoupons.getReceiveDate(), financingActivityInterstCoupons.getIcValidityDay()).getTime() - new Date().getTime() < 0) {
                    interstCoupons.setIcStatus(3);
                    log.info("加息券订单已过期");
                }
            }

        }
        if (interstCoupons.getIcStatus() == 3) {
            userInterstCouponsRelationRepository.save(interstCoupons);
        }

    }

    @Transactional
    public void InterstCouponsCalculate(UserInterstCouponsRelation userInterstCouponsRelation) throws Exception {

        //加息券返回都是BCB，其中结算进流水，到余额。本金进基金公司余额，利息进用户余额，利息计算方式为【本金 * 加息券额度 * 时长（天） / 365】
        FinancingActivityInterstCoupons financingActivityInterstCoupons = financingActivityInterstCouponsRepository.findByIcId(userInterstCouponsRelation.getIcId());
        if (ObjectUtils.isEmpty(financingActivityInterstCoupons)) {
            log.error("加息券项目不存在，无法对订单进行结算: " + userInterstCouponsRelation);
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserFinancingRecord userFinancingRecord = userFinancingRecordRepository.findOneById(userInterstCouponsRelation.getRecordId());
        Integer dayNum = CommonUtil.surplusDay(simpleDateFormat.format(userInterstCouponsRelation.getIcBeginTime()),simpleDateFormat.format(userFinancingRecord.getExpireTime()));
        BigDecimal amountUSDX = BigDecimal.ZERO;
        if(dayNum > CommonUtil.surplusDay(userInterstCouponsRelation.getIcBeginTime(), userInterstCouponsRelation.getIcEndTime())){
            amountUSDX = amountUSDX.add(userFinancingRecord.getUsdxAmount().multiply(financingActivityInterstCoupons.getIcRate()).multiply(BigDecimal.valueOf(CommonUtil.surplusDay(userInterstCouponsRelation.getIcBeginTime(), userInterstCouponsRelation.getIcEndTime()))).divide(BigDecimal.valueOf(365), 9, BigDecimal.ROUND_HALF_DOWN));
        }else {
            amountUSDX = amountUSDX.add(userFinancingRecord.getUsdxAmount().multiply(financingActivityInterstCoupons.getIcRate()).multiply(BigDecimal.valueOf(dayNum)).divide(BigDecimal.valueOf(365), 9, BigDecimal.ROUND_HALF_DOWN));
        }
        //判断用户是否拥有钱包地址
        userInterstCouponsRelation.setIcStatus(2);
        userInterstCouponsRelation.setIcSeletment(amountUSDX);
        userInterstCouponsRelation.setCoinName("USDX");
        userInterstCouponsRelation.setCoinId(systemCoinRepository.findByName("USDX_BCB").getId());
        userInterstCouponsRelationRepository.save(userInterstCouponsRelation);

    }

    //获取多少天之后
    public Date StrTime(Date dateStr, Integer past) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateStr);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        String result = sdf.format(today);
        return sdf.parse(result);
    }


}
