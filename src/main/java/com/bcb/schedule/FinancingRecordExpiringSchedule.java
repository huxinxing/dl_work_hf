package com.bcb.schedule;

import com.alibaba.fastjson.JSONObject;
import com.bcb.domain.repository.UserFinancingRecordRepository;
import com.bcb.domain.entity.PushRecord;
import com.bcb.domain.entity.UserFinancingRecord;
import com.bcb.domain.enums.PushExtraTypeEnum;
import com.bcb.domain.enums.UserFinancingRecordStatusEnum;
import com.bcb.service.PushRecordService;
import com.bcb.service.PushUserService;
import com.bcb.service.UserFinancingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;


/**
 * 每小时检查一次即将到期投资记录，发送即将到期通知提醒
 */
@Slf4j
@Component
public class FinancingRecordExpiringSchedule {
    @Autowired
    UserFinancingRecordRepository userFinancingRecordRepository;

    @Autowired
    UserFinancingRecordService userFinancingRecordService;

    @Autowired
    PushRecordService pushRecordService;

    @Autowired
    PushUserService pushUserService;

    @Scheduled(fixedDelay = 30*60*1000)   // 30 分钟推送一个订单
    public void pushCompletingFinancingMessage(){
        try {
            log.debug("启动定时任务：检查即将到期任务，发送即将到期通知");
            // 获取距到期还有23-24小时的投资信息，按时间顺序，分页获取，然后逐个发送推送消息
            Timestamp start = new Timestamp(System.currentTimeMillis() + 23 * 60 * 60 * 1000L);
            Timestamp end = new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000L); // 一个小时后
            int page = 0;
            while (true) {
                Pageable pageable = PageRequest.of(page, 50, new Sort(Sort.Direction.ASC, "expireTime"));
                Page<UserFinancingRecord> records = userFinancingRecordRepository.findAll(build(start, end), pageable);
                if (records == null) {
                    break;
                }
                if (!records.hasContent()) {
                    break;
                }
                for (UserFinancingRecord record : records) {
                    // 发送消息
                    pushMessage(record);
                }

                // 判断还有没有下一页
                if (!records.hasNext()) {
                    break;
                }
                page++;
            }
            log.debug("定时任务结束：检查即将到期任务，发送即将到期通知");
        } catch(Exception ex){
            log.error("定时任务：检查即将到期任务，发送即将到期通知，执行过程中异常：", ex);
        }
    }

    private Specification build(Timestamp start, Timestamp end) {
        return new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate pred = criteriaBuilder.equal(root.get("recordStatus"), UserFinancingRecordStatusEnum.进行中.getValue());
                if (start != null && end != null && start.before(end)) {
                    Predicate tmp = criteriaBuilder.between(root.get("expireTime"), start, end);
                    return criteriaBuilder.and(pred, tmp);
                }
                return pred;
            }
        };
    }

    private void pushMessage(UserFinancingRecord record) {
        try {
            PushRecord pr = new PushRecord();
            pr.setUserId(record.getUserId());
            pr.setState(false);
            pr.setCreateTime(new Timestamp(System.currentTimeMillis()));

            pr.setTitle("理财到期提醒");
            pr.setMessage("您有理财产品即将到期，资金预计在3天内到帐，请注意查收");
            pr.setTargetId(pushUserService.get(record.getUserId()));
            pr.setExtraType(PushExtraTypeEnum.ORDER_COMPLETING.getValue());
            JSONObject value = new JSONObject();
            value.put("tsId", record.getTxId());
            pr.setExtraValue(value.toJSONString());
            pr.setId(null);

            pushRecordService.add(pr);
            log.info("推送订单成功消息完成：" + pr.toString());
        } catch (Exception ex) {
            log.error("推送订单(" + record.getId() + ")消息失败：", ex);
        }
    }
}
