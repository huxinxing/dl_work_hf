package com.bcb.schedule;

import java.sql.Date;

import com.bcb.domain.repository.UserFinancingRecordRepository;
import com.bcb.domain.entity.UserFinancingRecord;
import com.bcb.service.PushRecordService;
import com.bcb.service.PushUserService;
import com.bcb.service.UserFinancingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 每小时10分钟时查看有没有理财订单到期，到期后，则修改状态为理财结束，不做计算，仅修改状态
 */
@Slf4j
@Component
public class FinancingRecordExpiredSchedule {
    @Autowired
    UserFinancingRecordRepository userFinancingRecordRepository;

    @Autowired
    UserFinancingRecordService userFinancingRecordService;

    @Autowired
    PushRecordService pushRecordService;

    @Autowired
    PushUserService pushUserService;

    @Scheduled(fixedDelay = 1000)   //1秒钟进行一次投资状态修改
    public void checkFinancingRecordExpire(){
        log.debug("启动定时任务：执行修改理财到期订单状态");
        UserFinancingRecord userFinancingRecord = null;
        try {
            userFinancingRecord = userFinancingRecordRepository.findFirstByRecordStatusAndExpireTimeBeforeOrderByExpireTimeDesc(UserFinancingRecord.RECORD_STATUS_ONGOING, new Date(System.currentTimeMillis()));
            if (ObjectUtils.isEmpty(userFinancingRecord)) {
                log.debug("没有到期的投资记录");
                return;
            }
        } catch (Exception e) {
            log.error("获取需要结算理财到期的投资记录异常：", e);
            return;
        }

        try {
            userFinancingRecordService.updateStatus(userFinancingRecord, UserFinancingRecord.RECORD_STATUS_OVER);
        } catch (Exception ex) {
            log.error("修改到期投资记录状态异常：" + userFinancingRecord.toString(), ex);
            return;
        }

    }
}
