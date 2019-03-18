package com.bcb.schedule;

import com.bcb.domain.entity.UserInterstCouponsRelation;
import com.bcb.domain.repository.UserInterstCouponsRelationRepository;
import com.bcb.service.OperationManagerScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
public class InterstCouponsFailSchedule {

    @Autowired
    UserInterstCouponsRelationRepository userInterstCouponsRelationRepository;

    @Autowired
    OperationManagerScheduleService operationManagerScheduleService;

    @Scheduled(fixedDelay = 1000)   //1秒钟进行一次投资状态修改
    public void checkGoldExperienceFailExpire() throws Exception {
        log.debug("启动定时任务：对到期加息券订单修改订单状态");
        UserInterstCouponsRelation userInterstCouponsRelation = null;
        try {
            // 查询到期理财记录
            userInterstCouponsRelation = userInterstCouponsRelationRepository.findFirstByIcStatusOrderByReceiveDateDesc(0);
            if (ObjectUtils.isEmpty(userInterstCouponsRelation)) {
                log.debug("没有到期的加息券");
                return;
            }
        } catch (Exception e) {
            log.error("修改无效加息券状态定时器异常：", e);
            return;
        }

        try {
            operationManagerScheduleService.interstCouponsFailCheck(userInterstCouponsRelation);
        } catch (Exception e) {
            log.error("加息券无效检验异常 interstCoupons:" + userInterstCouponsRelation.toString(), e);
            return;
        }

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
