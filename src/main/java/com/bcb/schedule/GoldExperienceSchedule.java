package com.bcb.schedule;

import com.bcb.domain.entity.*;
import com.bcb.domain.repository.*;
import com.bcb.service.OperationManagerScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.sql.Date;



//体验金自动结算
@Slf4j
@Component
public class GoldExperienceSchedule {

    @Autowired
    UserGoldexperienceRelationRepository userGoldexperienceRelationRepository;

    @Autowired
    OperationManagerScheduleService operationManagerScheduleService;

    @Scheduled(fixedDelay = 1000)   //1秒钟执行一次体验金结算
    public void settleGoldExperience() throws Exception {
        log.debug("启动定时任务：对到期体验金订单进行结算");
        UserGoldexperienceRelation userGoldexperienceRelation = null;

        try{
            userGoldexperienceRelation = userGoldexperienceRelationRepository.findFirstByExperienceStatusAndExperienceEndTimeBefore(1,new Date(System.currentTimeMillis()));   //状态1表示计息中
            if(ObjectUtils.isEmpty(userGoldexperienceRelation)){
                log.debug("没有体验金到期项目");
                return;
            }
        }catch (Exception e){
            log.error("获取到期的体验金项目异常");
            return;
        }

        try{
            operationManagerScheduleService.goldExperienceCalculate(userGoldexperienceRelation);
        }catch (Exception e){
            log.error("体验金计算异常 userGoldexperienceRelation:" + userGoldexperienceRelation.toString(),e);
            return;
        }


    }
}
