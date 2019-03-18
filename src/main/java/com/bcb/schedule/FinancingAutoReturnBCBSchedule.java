package com.bcb.schedule;

import com.alibaba.fastjson.JSONObject;
import com.bcb.bean.SystemProperties;
import com.bcb.domain.entity.*;
import com.bcb.domain.repository.*;
import com.bcb.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Slf4j
@Component
public class FinancingAutoReturnBCBSchedule {

    @Autowired
    BlockOperationInfoRepository blockOperationInfoRepository;

    private Boolean execute_returnBCBFlag = false;

    @Autowired
    UserBalanceWithdrawRepository userBalanceWithdrawRepository;

    @Autowired
    UserBalanceRepository userBalanceRepository;

    @Autowired
    UserTransferRepository userTransferRepository;

    @Autowired
    SystemProperties systemProperties;

    public void autoReturnBcbSchedule(BlockOperationInfo blockOperationInfo) throws Exception {
        String businessId = "";
        // 先置位操作记录状态
        blockOperationInfo.setStatue(BlockOperationInfo.BLOCK_OPERATION_INFO_STATUS_TWO); // 已操作状态
        blockOperationInfoRepository.save(blockOperationInfo);

        businessId = blockOperationInfo.getBusinessId();
        String[] arrBusinessId = businessId.split("_");
        // 执行区块打币操作
        if (StringUtils.isEmpty(blockOperationInfo.getToToken())) {
            throw new Exception("自动打币失败:打币地址不能为空：" + blockOperationInfo.toString());
        }
        String response = CommonUtil.sendBcB(blockOperationInfo.getToToken(), blockOperationInfo.getAmount(), systemProperties.getMidUrl());
        JSONObject responseObj = JSONObject.parseObject(response);
        if (responseObj.get("code").equals("200")) {   //原先为代理提币接口，现在改为余额提币接口
            if ("fagents".equals(arrBusinessId[0])) {
                UserBalanceWithdraw draw = userBalanceWithdrawRepository.findOneById(Integer.valueOf(arrBusinessId[1]));
                UserBalance userBalance = userBalanceRepository.findByUserIdAndCoinId(draw.getUserId(), draw.getCoinId());
                if (null == draw) {
                    log.info("~~~~~没有余额提币记录~~~~~businessId:" + businessId);
                    throw new Exception("~~~~~没有余额提币记录~~~~~businessId:" + businessId);
                }
                draw.setTxId(responseObj.getString("data"));
                draw.setStatus(UserBalanceWithdraw.FINANCING_WITHDRAW_STATUS_VERIFIED);
                userBalanceWithdrawRepository.save(draw);

                blockOperationInfo.setTxId(responseObj.getString("data"));
                blockOperationInfoRepository.save(blockOperationInfo);

                UserTransfer withdrawTransfer = new UserTransfer();
                withdrawTransfer.setAmount(blockOperationInfo.getAmount());
                withdrawTransfer.setUserId(draw.getUserId());
                withdrawTransfer.setCoinId(draw.getCoinId());
                withdrawTransfer.setFromAddress(draw.getToAddress());
                withdrawTransfer.setToAddress(draw.getToAddress());
                withdrawTransfer.setFee(BigDecimal.valueOf(0.1));
                withdrawTransfer.setBalance(userBalance.getBalance());
                withdrawTransfer.setModifyTime(new Timestamp(System.currentTimeMillis()));
                withdrawTransfer.setType(1);
                withdrawTransfer.setRemark("用户 " + draw.getUserId() + " 提币");

                userTransferRepository.save(withdrawTransfer);
                userBalance.setFrozen(BigDecimal.ZERO);
                userBalanceRepository.save(userBalance);
            }
        } else {
            log.error("-----打币失败，businessId：" + businessId + "，失败信息：" + response);
        }
    }


    /**
     * 定时扫描区块操作队列，执行区块操作，真实打币
     * 遇到异常需要人工打币
     *
     * @throws Exception
     */
    @Scheduled(fixedDelay = 1000)   //1秒钟进行一次打币操作
    public void execute_returnBCB() {
        log.debug("启动定时任务：执行余额提币打币操作!");
        // 防止代码重入，防止多线程
        if (execute_returnBCBFlag) {
            log.error("定时扫描区块操作队列，执行区块操作正在进行中，放弃本次操作，不允许重入操作");
            return;
        }
        execute_returnBCBFlag = true;
        try {
            // 获取已登记，未操作区块记录
            BlockOperationInfo blockOperationInfo = blockOperationInfoRepository.findFirstByStatue(0);
            if (ObjectUtils.isEmpty(blockOperationInfo)) {
                log.debug("~~~~~没有待处理区块~~~~~");
                execute_returnBCBFlag = false;
                return;
            }
            try {
                log.info("用户提币信息：" + blockOperationInfo.toString());
                autoReturnBcbSchedule(blockOperationInfo);
            } catch (Exception e) {
                log.error("自动结算bcb失败 getBusinessId：" + blockOperationInfo.toString(), e);
                return;
            }
        } catch (Exception ex) {
             log.error("定时扫描区块操作队列定时器异常，退出定时器：", ex);
            return;
        } finally {
            execute_returnBCBFlag = false;
        }

    }
}
