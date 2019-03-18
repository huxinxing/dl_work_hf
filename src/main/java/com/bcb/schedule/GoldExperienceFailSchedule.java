package com.bcb.schedule;

import com.bcb.domain.entity.UserGoldexperienceRelation;
import com.bcb.domain.repository.UserGoldexperienceRelationRepository;
import com.bcb.service.OperationManagerScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
@Slf4j
@Component
public class GoldExperienceFailSchedule {

    @Autowired
    UserGoldexperienceRelationRepository userGoldexperienceRelationRepository;

    @Autowired
    OperationManagerScheduleService operationManagerScheduleService;

    @Scheduled(fixedDelay = 1000)   //1秒钟进行一次投资状态修改
    public void checkGoldExperienceFailExpire() throws Exception {
        log.debug("启动定时任务：对到期体验金项目修改状态");
        UserGoldexperienceRelation userGoldexperienceRelation = null;
        try {
            userGoldexperienceRelation = userGoldexperienceRelationRepository.findFirstByExperienceStatusOrderByReceiveDateDesc(0);
            if (ObjectUtils.isEmpty(userGoldexperienceRelation)) {
                log.debug("没有到期的体验金:");
                return;
            }
        } catch (Exception e) {
            log.error("修改无效体验金状态定时器异常：", e);
            return;
        }

        try {
            operationManagerScheduleService.goldExperienceFailCheck(userGoldexperienceRelation);
        } catch (Exception e) {
            log.error("体验金检验是否有效异常 goldexperience:" + userGoldexperienceRelation.toString(), e);
            return;
        }

    }

}
