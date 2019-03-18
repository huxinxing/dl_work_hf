package com.bcb.bean.dto.statistics;

import java.math.BigDecimal;
import java.util.Date;

public class StatReceivedFlowsDto {
	
	private String createTime; //创建时间
	
	private BigDecimal receivedPrincipal; // 已收本金
	
	private BigDecimal receivedPaymentFee; // 已收认购费
	
	private BigDecimal receivedServiceFee; // 已收服务费
	
	private BigDecimal receivedAchievements; // 已收基金公司绩效佣金
	
	private BigDecimal receivedTotal;       // 已收总计，usdx统计
	
	private BigDecimal receivedTax;     //代收税费
	
	private String tradeConfirmTime;           // 订单结算时间
	
	public StatReceivedFlowsDto() {}
	
	public StatReceivedFlowsDto(String createTime, BigDecimal receivedPrincipal, BigDecimal receivedPaymentFee,
			BigDecimal receivedServiceFee, BigDecimal receivedAchievements, BigDecimal receivedTotal,
			BigDecimal receivedTax, String tradeConfirmTime) {
		this.createTime = createTime;
		this.receivedPrincipal = receivedPrincipal;
		this.receivedPaymentFee = receivedPaymentFee;
		this.receivedServiceFee = receivedServiceFee;
		this.receivedAchievements = receivedAchievements;
		this.receivedTotal = receivedTotal;
		this.receivedTax = receivedTax;
		this.tradeConfirmTime = tradeConfirmTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public BigDecimal getReceivedPrincipal() {
		return receivedPrincipal;
	}

	public void setReceivedPrincipal(BigDecimal receivedPrincipal) {
		this.receivedPrincipal = receivedPrincipal;
	}

	public BigDecimal getReceivedPaymentFee() {
		return receivedPaymentFee;
	}

	public void setReceivedPaymentFee(BigDecimal receivedPaymentFee) {
		this.receivedPaymentFee = receivedPaymentFee;
	}

	public BigDecimal getReceivedServiceFee() {
		return receivedServiceFee;
	}

	public void setReceivedServiceFee(BigDecimal receivedServiceFee) {
		this.receivedServiceFee = receivedServiceFee;
	}

	public BigDecimal getReceivedAchievements() {
		return receivedAchievements;
	}

	public void setReceivedAchievements(BigDecimal receivedAchievements) {
		this.receivedAchievements = receivedAchievements;
	}

	public BigDecimal getReceivedTotal() {
		return receivedTotal;
	}

	public void setReceivedTotal(BigDecimal receivedTotal) {
		this.receivedTotal = receivedTotal;
	}

	public BigDecimal getReceivedTax() {
		return receivedTax;
	}

	public void setReceivedTax(BigDecimal receivedTax) {
		this.receivedTax = receivedTax;
	}

	public String getTradeConfirmTime() {
		return tradeConfirmTime;
	}


	public void setTradeConfirmTime(String tradeConfirmTime) {
		this.tradeConfirmTime = tradeConfirmTime;
	}
}
