package com.bcb.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.bcb.bean.dto.UserInvestRateDto;
import com.bcb.bean.dto.financing.UserInverstDetailDto;
import com.bcb.bean.dto.financing.UserInverstStatisticsResponse;
import com.bcb.bean.dto.statistics.FinancingWithDrawDto;
import com.bcb.domain.dao.FinancingDao;
import com.bcb.domain.dao.FinancingQueryDao;
import com.bcb.domain.entity.*;
import com.bcb.domain.enums.UserFinancingRecordStatusEnum;
import com.bcb.domain.repository.*;
import com.bcb.exception.MyGlobalInternalException;
import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bcb.annotation.ActionType;
import com.bcb.bean.JSONParam;
import com.bcb.bean.ResponseResult;
import com.bcb.bean.WorldValue;
import com.bcb.bean.dto.FinancingTradeFlowVO;
import com.bcb.bean.dto.FinancingTradeVO;
import com.bcb.domain.enums.UnknowIncomeEnum;
import com.bcb.util.MapUtil;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Slf4j
@Data
@Service
public class FinancingService {
    @Autowired
    private FinancingBaseInfoRepository financingBaseInfoRepository;

    @Autowired
    private FinancingIncomeUnknownRepository financingIncomeUnknownRepository;

    @Autowired
    private UserFinancingRecordRepository userFinancingRecordRepository;

    @Autowired
    private UserBalanceWithdrawRepository userBalanceWithdrawRepository;

    @Autowired
    private UserWalletInfoRepository userWalletInfoRepository;

    @Autowired
    private FinancingWalletInfoRepository financingWalletInfoRepository;

    @Autowired
    private FinancingQueryDao financingQueryDao;

    @Autowired
    private BlockOperationInfoRepository blockOperationInfoRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private FinancingDao financingDao;

    @Autowired
    private CoinRepository coinRepository;

    @Autowired
    SystemConfigurationRepository systemConfigurationRepository;

    @Autowired
    SystemCoinRepository systemCoinRepository;

    @Autowired
    UserBalanceRepository userBalanceRepository;


    public String queryFinancingBaseInfoList(JSONParam[] params) throws Exception {
        Map<String, String> paramMap = MapUtil.convertToMap(params);
        String sEcho = paramMap.get("sEcho");
        Integer start = Integer.parseInt(paramMap.get("iDisplayStart"));
        Integer length = Integer.parseInt(paramMap.get("iDisplayLength"));


        //FinancingBaseInfo financingBaseInfo = new FinancingBaseInfo();
        //financingBaseInfo.setStatus(Integer.parseInt(paramMap.get("status")));
        //Map<String, Object> retmap = financingDao.queryFinancingList(financingBaseInfo,start, length);
        Pageable pageable = PageRequest.of(start / length, length, new Sort(Sort.Direction.DESC, "createTime"));
        Page<FinancingBaseInfo> infos = financingBaseInfoRepository.findAll(new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate pred = null;
                Integer status = null;
                try {
                    status = Integer.parseInt(paramMap.get("status"));
                } catch (Exception ex) {

                }
                String titleOrSerial = paramMap.getOrDefault("financing", null);
                if (status != null) {
                    pred = criteriaBuilder.equal(root.get("status"), status);
                }
                if (!StringUtils.isEmpty(titleOrSerial)) {
                    Predicate tmp1 = criteriaBuilder.like(root.get("title"), "%" + titleOrSerial + "%");
                    Predicate tmp2 = criteriaBuilder.like(root.get("serialNum"), "%" + titleOrSerial + "%");
                    if (pred == null) {
                        pred = criteriaBuilder.or(tmp1, tmp2);
                    } else {
                        Predicate tmp3 = criteriaBuilder.or(tmp1, tmp2);
                        pred = criteriaBuilder.and(pred, tmp3);
                    }
                }
                return pred;
            }
        }, pageable);

        StringBuilder stringJson = null;
        //Integer renum = (Integer) retmap.get("num");
        List<FinancingBaseInfo> accounts = infos.getContent();//(List<FinancingBaseInfo>) retmap.get("items");
        stringJson = new StringBuilder("{\"sEcho\":" + sEcho
                + ",\"iTotalRecords\":" + infos.getTotalElements() + ",\"iTotalDisplayRecords\":"
                + infos.getTotalElements() + ",\"aaData\":[");
        FinancingBaseInfo obj = null;
        for (int i = 0; i < accounts.size(); i++) {
            obj = accounts.get(i);
            stringJson.append("[");
            stringJson.append("\"" + obj.getSerialNum() + "\",");
            stringJson.append("\"" + obj.getTitle() + "\",");
            stringJson.append("\"" + obj.getCoinType() + "\",");
            stringJson.append("\"" + obj.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP) + "\",");
            stringJson.append("\"" + obj.getFreezeNumber() + obj.getFreezeUnit() + "\",");
            stringJson.append("\"" + obj.getAnnualRate().setScale(2, BigDecimal.ROUND_HALF_UP) + "%" + "\",");
            stringJson.append("\"" + obj.getCoinMinLimit().setScale(2, BigDecimal.ROUND_HALF_UP) + "-" + obj.getCoinLimit().setScale(2, BigDecimal.ROUND_HALF_UP) + "\",");
            stringJson.append("\"" + dateStr(obj.getCreateTime(), "yyyy-MM-dd HH:mm:ss") + "\",");
            stringJson.append("\"" + obj.getCreateBcb2Usdx().setScale(3, BigDecimal.ROUND_HALF_UP) + "\",");
            stringJson.append("\"" + obj.getFinancingUuid() + "\",");
            stringJson.append("\"" + "" + "\"");
            stringJson.append("],");
        }
        if (accounts.size() > 0) {
            stringJson.deleteCharAt(stringJson.length() - 1);
        }
        stringJson.append("]");
        stringJson.append("}");
        String result = stringJson.toString();
        return result.replaceAll("\r\n", "</br>");
    }


    /**
     * @author qiang wen
     * @description 根据UUID 获取理财项目基本信息
     */
    public FinancingBaseInfo queryFinancingBaseInfoByUuid(String uuid) {
        return financingBaseInfoRepository.findOneByFinancingUuid(uuid);
    }

    /**
     * @return java.lang.String
     * @author qiang wen
     * @description 查询交易管理信息
     */
    public String queryFinancingTradeManagerList(JSONParam[] params) {
        Map<String, String> paramMap = MapUtil.convertToMap(params);
        String sEcho = paramMap.get("sEcho");
        Integer start = Integer.parseInt(paramMap.get("iDisplayStart"));
        Integer length = Integer.parseInt(paramMap.get("iDisplayLength"));
        FinancingTradeVO financingTrade = new FinancingTradeVO();
        financingTrade.setStatus(Integer.parseInt(paramMap.get("status")));
        financingTrade.setFromToken(paramMap.get("search"));
        Map<String, Object> retmap = financingDao.queryFinancingTradeManagerList(financingTrade, start, length);
        StringBuilder stringJson = null;
        Integer renum = (Integer) retmap.get("num");
        List<FinancingTradeVO> accounts = (List<FinancingTradeVO>) retmap.get("items");
        stringJson = new StringBuilder("{\"sEcho\":" + sEcho
                + ",\"iTotalRecords\":" + renum + ",\"iTotalDisplayRecords\":"
                + renum + ",\"aaData\":[");
        FinancingTradeVO obj = null;
        for (int i = 0; i < accounts.size(); i++) {
            obj = accounts.get(i);
            stringJson.append("[");
            stringJson.append("\"" + obj.getSerialNum() + "\",");
            stringJson.append("\"" + obj.getTradeId() + "\",");
            stringJson.append("\"" + obj.getProjectName() + "\",");
            stringJson.append("\"" + obj.getUserName() + "\",");
            stringJson.append("\"" + obj.getFromToken() + "\",");
            stringJson.append("\"" + obj.getToToken() + "\",");
            stringJson.append("\"" + obj.getPaymentAmount() + obj.getPaymentType() + "\",");
            stringJson.append("\"" + obj.getUsdxAmount() + "USDX" + "\",");
            stringJson.append("\"" + (obj.getSuperiorRate().compareTo(new BigDecimal("0")) == 0 ? "0" : obj.getSuperiorRate()) + "\",");
            stringJson.append("\"" + obj.getConfirmTime() + "\",");
            stringJson.append("\"" + obj.getTsId() + "\"");
            stringJson.append("],");
        }
        if (accounts.size() > 0) {
            stringJson.deleteCharAt(stringJson.length() - 1);
        }
        stringJson.append("]");
        stringJson.append("}");
        String result = stringJson.toString();
        return result;
    }

    public List<UserInvestRateDto> queryAgentTradeList(Integer tradeId) {
        return financingDao.queryAgentTradeList(tradeId);
    }

    /**
     * @author qiang wen
     * @description 查询理财交易流水信息
     */
    public String queryFinancingTradeFlowList(JSONParam[] params) {
        Map<String, String> paramMap = MapUtil.convertToMap(params);
        String sEcho = paramMap.get("sEcho");
        Integer start = Integer.parseInt(paramMap.get("iDisplayStart"));
        Integer length = Integer.parseInt(paramMap.get("iDisplayLength"));
        FinancingTradeFlowVO financingTradeFlow = new FinancingTradeFlowVO();
        financingTradeFlow.setPaymentType(paramMap.get("paymentType"));
        financingTradeFlow.setWalletAddr(paramMap.get("walletAddr"));
        financingTradeFlow.setStartTime(paramMap.get("createTimeStart"));
        financingTradeFlow.setEndTime(paramMap.get("createTimeEnd"));
        Map<String, Object> retmap = financingDao.queryFinancingTradeFlowList(financingTradeFlow, start, length);
        StringBuilder stringJson = null;
        Integer renum = (Integer) retmap.get("num");
        List<FinancingTradeFlowVO> accounts = (List<FinancingTradeFlowVO>) retmap.get("items");
        stringJson = new StringBuilder("{\"sEcho\":" + sEcho
                + ",\"iTotalRecords\":" + renum + ",\"iTotalDisplayRecords\":"
                + renum + ",\"aaData\":[");
        FinancingTradeFlowVO obj = null;
        for (int i = 0; i < accounts.size(); i++) {
            obj = accounts.get(i);
            stringJson.append("[");
            stringJson.append("\"" + obj.getSerialNum() + "\",");
            stringJson.append("\"" + obj.getTitle() + "\",");
            stringJson.append("\"" + obj.getAgentBillId() + "\",");
            stringJson.append("\"" + obj.getTradeId() + "\",");
            stringJson.append("\"" + obj.getUserName() + "\",");
            stringJson.append("\"" + obj.getUserId() + "\",");
            stringJson.append("\"" + obj.getPaymentAmount() + obj.getPaymentType() + "\",");
            stringJson.append("\"" + (obj.getInvestAmount() == null ? "" : obj.getInvestAmount()) + (obj.getInvestCoinType() == null ? "" : obj.getInvestCoinType()) + "\",");
            stringJson.append("\"" + (obj.getAgentRateAmount().compareTo(new BigDecimal("0")) == 0 ? "0" : obj.getAgentRateAmount()) + obj.getAgentCoinType() + "\",");
            stringJson.append("\"" + (obj.getTotalRate().compareTo(new BigDecimal("0")) == 0 ? "" : obj.getTotalRate()) + "\",");
            stringJson.append("\"" + (obj.getSuperiorRate().compareTo(new BigDecimal("0")) == 0 ? "" : obj.getSuperiorRate()) + "\",");
            stringJson.append("\"\",");
            stringJson.append("\"" + obj.getCreateTime() + "\"");
            stringJson.append("],");
        }
        if (accounts.size() > 0) {
            stringJson.deleteCharAt(stringJson.length() - 1);
        }
        stringJson.append("]");
        stringJson.append("}");
        String result = stringJson.toString();
        return result;
    }

    /**
     * @param user
     * @param type
     * @param pageSize
     * @param pageNum
     * @return java.lang.String
     * @author qiang wen
     * @description 代理收益提币审核信息
     */
    public PageInfo<FinancingWithDrawDto> queryFinancingWithDrawList(String user, Integer type, Integer pageSize, Integer pageNum) throws Exception {
        PageInfo<FinancingWithDrawDto> pageInfo = new PageInfo<>();
        if (type == 2) {
            type = 3;
        }
        try {
            Map<String, Object> retmap = financingDao.queryUserFinancingAgentsWithdrawList(type, user, (pageNum - 1) * pageSize, pageSize);
            List<FinancingWithDrawDto> list = (List<FinancingWithDrawDto>) retmap.get("items");
            for (int i = 0; i < list.size(); i++) {
                SystemCoin systemCoin = systemCoinRepository.findByName(list.get(i).getCoinName());
                UserBalance userBalance = userBalanceRepository.findByUserIdAndCoinId(Integer.parseInt(list.get(i).getUserId()), systemCoin.getId());
                if (ObjectUtils.isEmpty(userBalance)) {
                    list.get(i).setAmountBalance("-");
                } else {
                    list.get(i).setAmountBalance(userBalance.getBalance().toString());
                }
                list.get(i).setId(resultStrFormate(list.get(i).getId(), false));
                list.get(i).setUserId(resultStrFormate(list.get(i).getUserId(), false));
                list.get(i).setUserName(resultStrFormate(list.get(i).getUserName(),
                        false));
                list.get(i).setCoinName(resultStrFormate(list.get(i).getCoinName(), false));
                list.get(i).setAmount(resultStrFormate(list.get(i).getAmount(), false));
                list.get(i).setAddress(resultStrFormate(list.get(i).getAddress(), false));
                list.get(i).setCreateTime(resultStrFormate(list.get(i).getCreateTime(), false));
                list.get(i).setConfirmType(resultStrFormate(getStatusStr(Integer.parseInt(list.get(i).getConfirmType())), false));
                list.get(i).setReviewTime(resultStrFormate(list.get(i).getReviewTime(), false));
                list.get(i).setReviewer(resultStrFormate(list.get(i).getReviewer(), false));
                list.get(i).setRemark(resultStrFormate(list.get(i).getRemark(), false));
            }
            pageInfo.setTotal((Integer) retmap.get("num"));
            pageInfo.setList(list);
        } catch (Exception e) {
            log.error("获取提币审核列表失败");
            throw new Exception("获取提币审核列表失败", e);
        }
        return pageInfo;
    }

    private String getStatusStr(Integer status) {
        if (status == UserBalanceWithdraw.FINANCING_WITHDRAW_STATUS_REVIEWED) {
            return "确认中";
        } else if (status == UserBalanceWithdraw.FINANCING_WITHDRAW_STATUS_VERIFIED) {
            return "已完成";
        } else {
            return "-";
        }
    }

    private String formatStr(String str) {
        if (StringUtils.isNotBlank(str) && str != null) {
            return str;
        } else {
            return "";
        }
    }

    /**
     * @return com.bcb.bean.ResponseResult
     * @author qiang wen
     * @description 提币审核操作
     */
    @Transactional
    public ResponseResult approval(SystemLoginAccount account, Integer id, String refuseContain, String type) {
        // 根据id获取提现审核记录信息
        UserBalanceWithdraw draw = userBalanceWithdrawRepository.findOneById(id);


        if (draw.getCoinAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return new ResponseResult(WorldValue.ERROR, "500", "不是有效的金额" + draw.getCoinAmount());
        }

        if (draw.getStatus() != 0) {
            return new ResponseResult(WorldValue.ERROR, "500", "不是未审核的记录");
        }

        // 根据userid获取用户token
        String walletType = null;
        if(draw.getToAddress().substring(0,3).contains("bcb")){
            walletType = "bcb";
        }else {
            walletType = "ox";
        }
        UserWalletInfo wallet = new UserWalletInfo();
        List<UserWalletInfo> userWalletInfoList = userWalletInfoRepository.findByStatusBetweenAndUserId(UserWalletInfo.WALLET_STATUS_VALID,UserWalletInfo.WALLET_STATUS_VERIFIED,draw.getUserId());
        for (UserWalletInfo userWalletInfo : userWalletInfoList){
            if(userWalletInfo.getToken().substring(0,3).contains(walletType)){
                wallet = userWalletInfo;
                break;
            }
        }
        if (wallet == null) {
            return new ResponseResult(WorldValue.ERROR, "500", "没有找到有效的用户钱包");
        }

        log.info("用户" + id + "提现：" + draw.getCoinAmount());

        //修改返点审核记录
        //同意,只有同意的话，才会向区块链上打真钱
        if ("1".equals(type)) {
            if (draw.getCoinId() != 1 && draw.getCoinId() != 4) {
                SystemCoin systemCoin = systemCoinRepository.findOneById(draw.getCoinId());
                return new ResponseResult(WorldValue.ERROR, "500", "不支持以太坊" + systemCoin.getName() + "提币,目前只支持BCB主链提币。");
            }
            UserBalance userBalance = userBalanceRepository.findByUserIdAndCoinId(draw.getUserId(), draw.getCoinId());
            if (ObjectUtils.isEmpty(userBalance)) {
                return new ResponseResult(WorldValue.ERROR, "500", "余额账户不存在!");
            }
            draw.setStatus(UserBalanceWithdraw.FINANCING_WITHDRAW_STATUS_REVIEWED);
            // 登记返点提现转账区块操作记录
            String businessId = "fagent_" + draw.getId(); // 组装业务id
            BlockOperationInfo blockOperationInfo = blockOperationInfoRepository.findByBusinessId(businessId);
            if (null != blockOperationInfo) {
                log.info("===businessId:" + businessId + "已有区块操作记录，放弃本次区块操作登记");
                return new ResponseResult(WorldValue.SUCCESS, "200", "执行成功");
            }
            // 保存区块操作登记记录
            SystemCoin systemCoin = systemCoinRepository.findOneById(draw.getCoinId());
            blockOperationInfo = new BlockOperationInfo();
            blockOperationInfo.setCreateTime(new Date());
            blockOperationInfo.setBusinessId(businessId);
            blockOperationInfo.setStatue(0);// 初始状态
            blockOperationInfo.setCoinType(systemCoin.getName());
            blockOperationInfo.setToToken(wallet.getToken());
            blockOperationInfo.setAmount(draw.getCoinAmount());
            blockOperationInfoRepository.save(blockOperationInfo);
            log.info("===保存区块操作登记记录=====");
        }
        //拒绝
        if ("3".equals(type)) {
            UserBalance userBalance = userBalanceRepository.findByUserIdAndCoinId(draw.getUserId(), draw.getCoinId());
            userBalance.setFrozen(userBalance.getFrozen().subtract(draw.getCoinAmount()).subtract(draw.getHandlingCharge()));
            draw.setStatus(UserBalanceWithdraw.FINANCING_WITHDRAW_STATUS_REJUCT);
            draw.setRemark(refuseContain);
            userBalanceRepository.save(userBalance);
        }
        //修改返点审核记录
        draw.setReviewerId(account.getId());
        draw.setReviewTime(new Timestamp(System.currentTimeMillis()));
        userBalanceWithdrawRepository.save(draw);
        SystemLogOperation tempLog = new SystemLogOperation();
        Date now = new Date();
        tempLog.setCreateTime(now);
        tempLog.setCommentDescribe("余额提币审核");
        tempLog.setOperatorName(account.getLoginName());
        tempLog.setActionType(ActionType.UPDATE.getText());
        if ("1".equals(type)) {
            tempLog.setParameterContext("审核同意:" + "交易号" + id + "，用户ID" + draw.getUserId() + "，提现：" + draw.getCoinAmount());
        }
        if ("3".equals(type)) {
            tempLog.setParameterContext("审核拒绝:" + "交易号" + id + "，用户ID" + draw.getUserId() + "，提现：" + draw.getCoinAmount());
        }
        commonService.saveOperationLog(tempLog);

        return new ResponseResult(WorldValue.SUCCESS, "200", "执行成功");

    }

    /**
     * @author qiang wen
     * @description 代币处理
     */
    public ResponseResult FinancingUnknownIncomeDeal(SystemLoginAccount account, Integer id) {
        // 验证待处理记录
//        ProjectIncomeUnknown projectIncomeUnknown = commonService.queryProjectIncomeUnknown(id);
        FinancingIncomeUnknown financingIncomeUnknown = financingIncomeUnknownRepository.findById(id).get();
        if (financingIncomeUnknown == null) {
            return new ResponseResult(WorldValue.ERROR, "500", "请输入正确的ID号");
        }
        if (financingIncomeUnknown.getPaymentAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return new ResponseResult(WorldValue.ERROR, "500", "不是合法的投资金额");
        }
        if (financingIncomeUnknown.getCoinAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return new ResponseResult(WorldValue.ERROR, "500", "不是合法的代币金额");
        }
        if (StringUtils.isBlank(financingIncomeUnknown.getFromToken())) {
            return new ResponseResult(WorldValue.ERROR, "500", "不是合法的地址");
        }
        if (StringUtils.isBlank(financingIncomeUnknown.getTsId())) {
            return new ResponseResult(WorldValue.ERROR, "500", "不是合法的TxId");
        }
        if (financingIncomeUnknown.getStatus() == UnknowIncomeEnum.STATUS_DEALED.getValue()) {
            return new ResponseResult(WorldValue.ERROR, "500", "记录已处理");
        }

        // 查找用户关联钱包
        UserWalletInfo info = userWalletInfoRepository.findFirstByTokenAndStatusNot(financingIncomeUnknown.getFromToken(),UserWalletInfo.WALLET_STATUS_INVALID);
        if (ObjectUtils.isEmpty(info)) {
            return new ResponseResult(WorldValue.ERROR, "500", "该地址未被用户绑定或绑定地址已失效");
        }

        // 检查交易是否已处理
        if (userFinancingRecordRepository.countByTxId(financingIncomeUnknown.getTsId()) > 0) {
            return new ResponseResult(WorldValue.ERROR, "500", "已处理此交易记录");
        }

        SystemConfiguration config = systemConfigurationRepository.findByName(SystemConfiguration.SERVICE_RATE);

        // 生成新的UserFinancingRecord记录
        UserFinancingRecord record = new UserFinancingRecord();
        record.setRecordCreateTime(financingIncomeUnknown.getCreateTime());
        record.setBlockCreateTime(financingIncomeUnknown.getCreateTime());
        record.setUserId(info.getUserId());
        record.setFinancingUuid(financingIncomeUnknown.getFinancingUuid());
        record.setFromAddress(financingIncomeUnknown.getFromToken());
        record.setToAddress(financingIncomeUnknown.getToToken());
        record.setAmount(financingIncomeUnknown.getPaymentAmount());
        Coin coin = coinRepository.findByName(financingIncomeUnknown.getPaymentType());
        if (!ObjectUtils.isEmpty(coin)) {
            record.setCoinId(coin.getId());
        }
        record.setCoinName(financingIncomeUnknown.getPaymentType());
        record.setTxId(financingIncomeUnknown.getTsId());
        record.setBcbAmount(financingIncomeUnknown.getCoinAmount());
        record.setConfirmNum(financingIncomeUnknown.getConfirmNum());
        record.setAgentsStatus(ObjectUtils.isEmpty(financingIncomeUnknown.getAgentRebateStatus()) ? 0 : financingIncomeUnknown.getAgentRebateStatus());
        record.setUsdxAmount(financingIncomeUnknown.getUsdxAmount());
        record.setBcb2usdx(financingIncomeUnknown.getBcb2Usdx());
        record.setCoin2bcb(financingIncomeUnknown.getCoin2Bcb());
        record.setAnnualRate(financingIncomeUnknown.getTotalRate().divide(BigDecimal.valueOf(100)).setScale(9, BigDecimal.ROUND_HALF_DOWN));
        record.setServiceRate(new BigDecimal(config.getValue()));
        record.setSuperiorCommissionRate(financingIncomeUnknown.getSuperiorRate().divide(BigDecimal.valueOf(100)).setScale(9, BigDecimal.ROUND_HALF_DOWN));
        record.setRedeemptionStatus(0);
        record.setBlockNum(financingIncomeUnknown.getBlockNum());
        record.setFoundationCommissionRate(financingIncomeUnknown.getFoundationRate().divide(BigDecimal.valueOf(100)).setScale(9, BigDecimal.ROUND_HALF_DOWN));
        FinancingBaseInfo financingBaseInfo = financingBaseInfoRepository.findOneByFinancingUuid(financingIncomeUnknown.getFinancingUuid());
        if (!financingBaseInfo.getPaymentType().contains(financingIncomeUnknown.getPaymentType())) {
            record.setRecordStatus(UserFinancingRecordStatusEnum.参与失败.getValue());
        } else {
            record.setRecordStatus(UserFinancingRecordStatusEnum.进行中.getValue());
        }
        record.setExpireTime(expireTime(financingBaseInfo.getFreezeNumber(), financingBaseInfo.getFreezeUnit(), record.getRecordCreateTime()));
        record.setSubscriptionFee(ObjectUtils.isEmpty(financingIncomeUnknown.getSubscriptionFee()) ? BigDecimal.valueOf(0) : financingIncomeUnknown.getSubscriptionFee());
        record.setSubscriptionFeeRate(ObjectUtils.isEmpty(financingIncomeUnknown.getSubscriptionFeeRate()) ? BigDecimal.valueOf(0) : financingIncomeUnknown.getSubscriptionFeeRate().divide(BigDecimal.valueOf(100), 9, BigDecimal.ROUND_HALF_DOWN));

//        commonService.saveOrUpdateUserProjectRelation(relation);
        userFinancingRecordRepository.save(record);

        // 修改打币记录
        financingIncomeUnknown.setStatus(UnknowIncomeEnum.STATUS_DEALED.getValue());
        financingIncomeUnknownRepository.save(financingIncomeUnknown);
//        commonService.saveOrUpdateProjectIncomeUnknown(projectIncomeUnknown);
        SystemLogOperation tempLog = new SystemLogOperation();
        Date now = new Date();
        tempLog.setCreateTime(now);
        tempLog.setCommentDescribe("未绑定地址打币");
        tempLog.setOperatorName(account.getLoginName());
        tempLog.setActionType(ActionType.UPDATE.getText());
        tempLog.setParameterContext("打币处理" + ":交易ID" + id + "，交易金额" + financingIncomeUnknown.getCoinAmount());
        commonService.saveOperationLog(tempLog);
        return new ResponseResult(WorldValue.SUCCESS, "200", "操作成功");
    }

    /**
     * @description 查询理财项目钱包地址
     */
    public List<FinancingWalletInfo> queryFinancingWalletsByFinancingUuid(String financingUuid) {
        return financingWalletInfoRepository.findByFinancingUuid(financingUuid);
    }

    /**
     * 查询用户理财投资统计
     */
    public List<UserInverstStatisticsResponse> getInvestFinancingStatistics(Integer userId) {
        List<UserInverstStatisticsResponse> list = null;
        try {
            list = financingQueryDao.getInvestFinancingStatistics(userId);
        } catch (Exception e) {
            throw new MyGlobalInternalException("查询用户:" + userId + "投资理财统计异常", e);
        }
        return list;
    }


    /**
     * 查询用户理财投资详情的数量(注意同一个项目算一条)
     */
    public Integer getInvestFinancingCount(Integer userId) {
        Integer result = 0;
        try {
            result = financingQueryDao.getInvestFinancingCount(userId);
        } catch (Exception e) {
            throw new MyGlobalInternalException("查询用户:" + userId + "投资理财统计异常", e);
        }
        return result;
    }


    /**
     * 查询用户理财投资详情(注意同一个项目算一条)
     */
    public List<UserInverstDetailDto> getInvestFinancingList(Integer userId, Integer start, Integer length) {
        List<UserInverstDetailDto> list = null;
        try {
            list = financingQueryDao.getInvestFinancingList(userId, start, length);
        } catch (Exception e) {
            throw new MyGlobalInternalException("查询用户:" + userId + "投资理财统计异常", e);
        }
        return list;
    }


    //字符串转时间格式，DateStr为时间字符串，formate为转化的时间格式  "yyyy-MM-dd HH:mm:ss"
    public Date dateFormate(String DateStr, String formate) throws Exception {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(formate);
            Date date = formatter.parse(DateStr);
            return date;
        } catch (ParseException e) {
            throw new Exception("时间格式解析不正确");
        }
    }

    public String dateStr(Date date, String formate) throws Exception {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(formate);
            return formatter.format(date);
        } catch (Exception e) {
            throw new Exception("时间转字符串，转化格式不正确", e);
        }
    }

    //处理为空的字符串
    public String resultStrFormate(Object object, Boolean market) {
        Pattern pattern = Pattern.compile("^\\d+$|^\\d+\\.\\d+$|-\\d+$");
        if (ObjectUtils.isEmpty(object)) {
            return "-";
        } else if (pattern.matcher(object.toString()).matches() && market) {
            return new BigDecimal(object.toString()).setScale(3, BigDecimal.ROUND_HALF_UP).toString();
        } else {
            return object.toString();
        }
    }


    //创建到期时间
    public Timestamp expireTime(Integer FreezeNumber, String FreezeUnit, Timestamp createTime) {
        Calendar c = Calendar.getInstance();//获得一个日历的实例
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        c.setTime(createTime);//设置日历时间

        if (FreezeUnit.equals("年")) {
            c.add(Calendar.YEAR, FreezeNumber);
        } else if (FreezeUnit.equals("月")) {
            c.add(Calendar.MONTH, FreezeNumber);
        } else if (FreezeNumber.equals("天")) {
            c.add(Calendar.DAY_OF_YEAR, FreezeNumber);
        }
        String str = sdf.format(c.getTime());
        return Timestamp.valueOf(str);
    }

}
