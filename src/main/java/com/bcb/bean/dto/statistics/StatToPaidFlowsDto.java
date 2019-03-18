package com.bcb.bean.dto.statistics;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StatToPaidFlowsDto {
	// 待支付
	private String createTime;
	
	private BigDecimal toPaidFixedProfit;     // 待支付保底收益
	
	private BigDecimal toPaidAdditionalProfit; // 待支付额外收益
	
	private BigDecimal toPaidUSDX;           // 待支付金额(USDX) 收益
	
	private BigDecimal toPaidBCB;        // 待支付金额(BCB)
	
	private String trueConfirmTime; // 结算时间

}
