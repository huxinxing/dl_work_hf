package com.bcb.schedule;

import com.bcb.domain.repository.UserFinancingRecordRepository;
import com.bcb.domain.entity.*;
import com.bcb.service.UserFinancingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Slf4j
@Component
public class FinancingRecordSettlementSchedule {

    @Autowired
    UserFinancingRecordRepository userFinancingRecordRepository;

    @Autowired
    UserFinancingRecordService userFinancingRecordService;

    /**
     * 理财结算定时器(到期结算)
     */
    @Scheduled(fixedDelay =  1000)   //1秒钟进行一次结算
    public void settleFinancings() {
        log.debug("启动定时任务：执行理财到期订单自动结算");
        UserFinancingRecord userFinancingRecord = null;
        try {
            userFinancingRecord = userFinancingRecordRepository.findFirstByRecordStatus(UserFinancingRecord.RECORD_STATUS_OVER);
            if (ObjectUtils.isEmpty(userFinancingRecord)) {
                log.debug("没有需要结算的投资记录");
                return;
            }
        } catch (Exception e) {
            log.error("结算投资记录定时器异常：", e);
            return;
        }

        try {
            userFinancingRecordService.settle(userFinancingRecord);
        } catch (Exception ex) {
            log.error("结算投资记录异常：" + userFinancingRecord.toString(), ex);
            return;
        }

    }
}
