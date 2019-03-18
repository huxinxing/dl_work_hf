package com.bcb.bean.dto.statistics;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatFinancingProjectDetailDto {
	
	private String Title;   //项目名称
	private String CreateTime;   //发布时间
	private String Status;  //项目状态
	private BigDecimal Amount;   //募集额度
	private String FinancingUuid;
	private BigDecimal CoinAmount;   //已募集
	private String schedules;   //进度
	private String humanNum;  //投资人数
	private BigDecimal totalInvest;  //投资金额

	private BigDecimal received;  //已收资金
	private BigDecimal paid;  //已支付收益
	private BigDecimal paidBCB; //已支付收益BCB总额

	private BigDecimal receivedPaymentFee;  //认购费
	private BigDecimal receivedServiceFee;  //服务费
	private BigDecimal receivedAchievements;   //基金公司绩效佣金
	private BigDecimal receivedTax;  //代收税费
	private BigDecimal receivedTotal;

	private BigDecimal paidPrincipal;  //返给用户的本金
	private BigDecimal paidAgentRebeat;  //项目返点
	private BigDecimal paidFixedProfit;  //保底收益
	private BigDecimal paidAdditionalProfit;  //额外收益
	private BigDecimal paidGeAmount;   //体验金
	private BigDecimal paidIcAmount;   //加息券

}
