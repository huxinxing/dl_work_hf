package com.bcb.bean.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatPaidFlowsDto {
	// 已支付
	private String createTime;

	private BigDecimal paidPrincipal;   //已支付本金

	private BigDecimal paidFixedProfit;     // 已支付保底收益

	private BigDecimal paidAdditionalProfit; // 已支付额外收益

	private BigDecimal paidAgentRebeat; // 已支付代理返点

	private BigDecimal paidGe;  //体验金

	private BigDecimal paidIc;  //加息券

	private BigDecimal paidUSDX;       // 已支付总额(USDX)

	private BigDecimal paidBCB;       // 已支付总额(BCB)
	
	private String trueConfirmTime; // 结算时间
}
