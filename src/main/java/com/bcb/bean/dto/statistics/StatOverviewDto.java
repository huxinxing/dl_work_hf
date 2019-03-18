package com.bcb.bean.dto.statistics;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatOverviewDto {
	private Integer financingCount;	 // 发布项目个数
	
	private BigDecimal totalInvest;	 // 获得投资总计，用USDX表示
	
	private BigDecimal totalInvestUSDX;	 // 获得USDX投资
	
	private BigDecimal totalInvestETH;	 // 获得ETH投资

	// 已收
	
	private BigDecimal receivedPrincipal; // 已收本金
	
	private BigDecimal receivedPaymentFee; // 已收认购费
	
	private BigDecimal receivedServiceFee; // 已收服务费
	
	private BigDecimal receivedAchievements; // 已收基金公司绩效佣金
	
	private BigDecimal receivedTax;  // 已收税费
	
	private String receivedWithdrawFee;  // 已收提币手续费，原型忘记写了    0.1BCB
	
	private BigDecimal receivedTotal;       // 已收总计，usdx统计
	
	// 已支付
	private BigDecimal paidPrincipal;  //已支付本金

	private BigDecimal paidFixedProfit;     // 已支付保底收益
	
	private BigDecimal paidAdditionalProfit; // 已支付额外收益
	
	private BigDecimal paidAgentRebeat; // 已支付项目返点

	private BigDecimal paidIC; //加息券

	private BigDecimal paidGe;   //体验金
	
	private BigDecimal paidTotal;       // 已支付总额(USDX)
	
	private BigDecimal paidTotalBCB;       // 已支付总额(BCB)
	
	// 待支付
	private BigDecimal toPaidFixedProfit;     // 待支付保底收益
	
	private BigDecimal toPaidAdditionalProfit; // 待支付额外收益
	
	private BigDecimal toPaidTotal;           // 待支付总额(USDX)
	
	private BigDecimal toPaidTotalBCB;        // 待支付总额(BCB)

}
