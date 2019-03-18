package com.bcb.bean.dto.statistics;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class StatDiagramsDto {
	private Integer length;              // 长度，和传入一致
	
	private List<BigDecimal> receivedTotals;	 // 收到资金总额
	
	private List<BigDecimal> paidTotals;	 // 支出资金总额
	
	private List<BigDecimal> toPaidTotals;	 // 待支出资金总额
	
	private List<String> Time;  // 时间

	public StatDiagramsDto() {}

	public StatDiagramsDto(List<BigDecimal> receivedTotals, List<BigDecimal> paidTotals,
			List<BigDecimal> toPaidTotals, List<String> time) {
		this.receivedTotals = receivedTotals;
		this.paidTotals = paidTotals;
		this.toPaidTotals = toPaidTotals;
		Time = time;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public List<BigDecimal> getReceivedTotals() {
		return receivedTotals;
	}

	public void setReceivedTotals(List<BigDecimal> receivedTotals) {
		this.receivedTotals = receivedTotals;
	}

	public List<BigDecimal> getPaidTotals() {
		return paidTotals;
	}

	public void setPaidTotals(List<BigDecimal> paidTotals) {
		this.paidTotals = paidTotals;
	}

	public List<BigDecimal> getToPaidTotals() {
		return toPaidTotals;
	}

	public void setToPaidTotals(List<BigDecimal> toPaidTotals) {
		this.toPaidTotals = toPaidTotals;
	}

	public List<String> getTime() {
		return Time;
	}

	public void setTime(List<String> time) {
		Time = time;
	}
	
}
