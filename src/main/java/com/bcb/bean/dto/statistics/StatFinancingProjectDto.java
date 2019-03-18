package com.bcb.bean.dto.statistics;

import java.math.BigDecimal;

public class StatFinancingProjectDto {
	
	private String FinancingUuid;   //项目id
	private String Title; 			//项目名称
	private String CreateTime; 		// 项目创建时间
	private String Status;			//项目状态
	private BigDecimal Amount;			//募集额度
	private String schedules;		//进度
	private BigDecimal received; 		//已收资金
	private BigDecimal paid; 			//已支付收益
	
	public StatFinancingProjectDto() {}

	public StatFinancingProjectDto(String financingUuid, String title, String createTime, String status,
			BigDecimal amount, String schedules, BigDecimal received, BigDecimal paid) {
		FinancingUuid = financingUuid;
		Title = title;
		CreateTime = createTime;
		Status = status;
		Amount = amount;
		this.schedules = schedules;
		this.received = received;
		this.paid = paid;
	}

	public String getFinancingUuid() {
		return FinancingUuid;
	}

	public void setFinancingUuid(String financingUuid) {
		FinancingUuid = financingUuid;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public BigDecimal getAmount() {
		return Amount;
	}

	public void setAmount(BigDecimal amount) {
		Amount = amount;
	}

	public String getSchedules() {
		return schedules;
	}

	public void setSchedules(String schedules) {
		this.schedules = schedules;
	}

	public BigDecimal getReceived() {
		return received;
	}

	public void setReceived(BigDecimal received) {
		this.received = received;
	}

	public BigDecimal getPaid() {
		return paid;
	}

	public void setPaid(BigDecimal paid) {
		this.paid = paid;
	}
	
}
