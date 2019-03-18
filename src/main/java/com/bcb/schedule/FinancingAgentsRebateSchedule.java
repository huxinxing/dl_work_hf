package com.bcb.schedule;

import java.sql.Timestamp;
import com.bcb.domain.repository.UserFinancingRecordRepository;
import com.bcb.domain.entity.PushRecord;
import com.bcb.domain.entity.UserFinancingRecord;
import com.bcb.domain.enums.PushExtraTypeEnum;
import com.bcb.service.PushRecordService;
import com.bcb.service.PushUserService;
import com.bcb.service.UserFinancingRecordService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;


/**
 * 计算代理收益：每10s钟计算一次代理收益返点
 */
@Slf4j
@Component
public class FinancingAgentsRebateSchedule {

	@Autowired
	UserFinancingRecordRepository userFinancingRecordRepository;

	@Autowired
	UserFinancingRecordService userFinancingRecordService;

	@Autowired
	PushRecordService pushRecordService;

	@Autowired
	PushUserService pushUserService;

	@Scheduled(fixedDelay = 1000)     //1s进行一次代理返点计算
	public void settleAgentsRebate(){
		// 查询到期理财记录
        log.debug("启动定时任务：执行代理收益结算");
		UserFinancingRecord record  = null;
		try{
			// 每次取一个记录
			record  = userFinancingRecordRepository.findFirstByRecordStatusAndAgentsStatus(
					UserFinancingRecord.RECORD_STATUS_ONGOING,UserFinancingRecord.AGENTS_STATUS_FAIL);
			if(record == null){
				log.debug("没有需要结算代理收益的投资记录");
				return;
			}
		}catch (Exception e){
			log.error("获取需要结算代理收益的投资记录异常：",e);
			return;
		}

		try{
			userFinancingRecordService.settleAgentsRebate(record);
		}catch (Exception e){
			log.error("订单代理收益结算失败 record：" + record, e);
			return;
		}
		// 结算完成，则发送理财成功通知消息
		// 此消息理论上应该在收到中间件回调，确认投资的时候发送的，此处先放在这里，后续再修改
		push(record);
	}

	private void push(UserFinancingRecord record){
		try{
			log.debug("推送订单成功消息："+record.toString());
			PushRecord pr = new PushRecord();
			pr.setUserId(record.getUserId());
			pr.setState(false);
			pr.setCreateTime(new Timestamp(System.currentTimeMillis()));
			pr.setExtraType(PushExtraTypeEnum.ORDER_SUCCESS.getValue());
			JSONObject value = new JSONObject();
			value.put("tsId", record.getTxId());
			pr.setExtraValue(value.toJSONString());
			pr.setMessage("订单成功，订单编号："+record.getId()+"，点击查看详情");
			pr.setTargetId(pushUserService.get(record.getUserId()));
			pr.setTitle("订单成功");
			pr.setId(null);

			pushRecordService.add(pr);
            log.info("推送订单成功消息完成："+ pr.toString());
		}catch (Exception ex){
			log.error("推送订单("+record+")消息失败：", ex);
		}
	}
}
