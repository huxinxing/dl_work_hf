package com.bcb.schedule;

import com.bcb.domain.entity.*;
import com.bcb.domain.repository.*;
import com.bcb.service.OperationManagerScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.Date;

//体验金自动结算
@Slf4j
@Component
public class InterstCouponsSchedule {

    @Autowired
    UserInterstCouponsRelationRepository userInterstCouponsRelationRepository;

    @Autowired
    OperationManagerScheduleService operationManagerScheduleService;

    @Scheduled(fixedDelay = 1000)   // 1 秒钟执行一次体验金结算
    public void settleGoldExperience() throws Exception {
        // 查询体验金到期体验金订单
        log.debug("启动定时任务：对到期加息券订单进行结算");
        UserInterstCouponsRelation userInterstCouponsRelation = null;
        try{
            userInterstCouponsRelation = userInterstCouponsRelationRepository.findFirstByIcStatusAndIcEndTimeBefore(1, new Date(System.currentTimeMillis()));   //状态1表示计息中
            if (ObjectUtils.isEmpty(userInterstCouponsRelation)) {
                log.debug("没有加息券到期项目");
                return;
            }
        }catch (Exception e){
            log.error("获取即将到期的加息券订单异常");
            return;
        }

        try {
            operationManagerScheduleService.InterstCouponsCalculate(userInterstCouponsRelation);
        } catch (Exception e) {
            log.error("加息券结算异常 userInterstCouponsRelation : " + userInterstCouponsRelation.toString(), e);
            return;
        }

    }

}
