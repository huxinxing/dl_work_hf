package com.bcb.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date 5/8/20182:51 PM
 */
@Data
@Entity
@Table(name="financing_income_unknown")
public class FinancingIncomeUnknown implements Serializable {

    public final static Integer UNKNOWN_INCOME_STATUS_INIT = 0;
    public final static Integer UNKNOWN_INCOME_STATUS_DEALED = 1;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Id", unique = true, nullable = false)
    private Integer id;//

    @Column(name="CreateTime", nullable = false)
    private Timestamp createTime;//

    @Transient
    private String createTimeStr;//

    @Column(name = "FinancingUuid", nullable = false)
    private String financingUuid;//

    @Column(name="FromToken")
    private String fromToken;//

    @Column(name="ToToken")
    private String toToken;//

    @Column(name="PaymentAmount")
    private BigDecimal paymentAmount;//

    @Column(name="PaymentType")
    private String paymentType;//

    @Column(name="TsId")
    private String tsId;//

    @Column(name="BlockNum")
    private Long blockNum;//

    @Column(name="CoinType")
    private String coinType;//

    @Column(name="CoinAmount")
    private BigDecimal coinAmount;//

    @Column(name="Expense")
    private BigDecimal expense;//
    
	@Column(name="FoundationRate")
	private BigDecimal foundationRate; // 超额收益基金公司返点比例

    @Column(name="ConfirmNum")
    private Integer confirmNum;//

    @Column(name="TradeConfirmTime")
    private Timestamp tradeConfirmTime;//

    @Transient
    private String tradeConfirmTimeStr;

    @Column(name="Status", nullable = false)
    private Integer status;//

    @Column(name="AgentRebateStatus")
    private Integer agentRebateStatus;//

    @Column(name="UsdxAmount")
    private BigDecimal usdxAmount;//

    @Column(name="Bcb2Usdx")
    private BigDecimal bcb2Usdx;//

    @Column(name="Usdx2Cny")
    private BigDecimal usdx2Cny;//

    @Column(name="Coin2Bcb")
    private BigDecimal coin2Bcb;//

    @Column(name="Quotation")
    private String quotation;//

    @Column(name="TotalRate")
    private BigDecimal totalRate;//

    @Column(name="SuperiorRate")
    private BigDecimal superiorRate;//

    @Column(name="SuperiorUsdxAmount")
    private BigDecimal superiorUsdxAmount;//

    @Column(name="SuperiorBcbAmount")
    private BigDecimal superiorBcbAmount;//

    @Column(name="TrueTotalRate")
    private BigDecimal trueTotalRate;//

    @Column(name="TrueBcbAmount")
    private BigDecimal trueBcbAmount;//

    @Column(name="TrueUsdxAmount")
    private BigDecimal trueUsdxAmount;//

    @Column(name="TrueBcb2Usdx")
    private BigDecimal trueBcb2Usdx;//

    @Column(name="TrueUsdx2Cny")
    private BigDecimal trueUsdx2Cny;//

    @Column(name="TrueExtraRate")
    private BigDecimal trueExtraRate;//

    @Column(name="TrueExtraBcbAmount")
    private BigDecimal trueExtraBcbAmount;//

    @Column(name="TrueExtraUsdxAmount")
    private BigDecimal trueExtraUsdxAmount;//

    @Column(name="TrueConfirmTime")
    private Date TrueConfirmTime;//

    @Column(name="SubscriptionFee")
    private BigDecimal SubscriptionFee;//

    @Column(name="SubscriptionFeeRate")
    private BigDecimal SubscriptionFeeRate;//

}
