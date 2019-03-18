package com.bcb.domain.entity;

import javax.persistence.*;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "financing_base_info")
public class FinancingBaseInfo {
    private static final long serialVersionUID = 5271367905881878559L;
    @Id
    @ApiModelProperty(value = "理财uuid",dataType = "String",required = false)
    @Column(name = "FinancingUuid", unique = true, nullable = false)
    private String financingUuid;

    @ApiModelProperty(value = "理财标题",required = true)
    @Column(name = "Title")
    private String title;

    @ApiModelProperty(value = "理财标签",required = true)
    @Column(name ="Tag")
    private String tag;

    @ApiModelProperty(value = "理财币种",required = true)
    @Column(name="CoinType")
    private String coinType;

    @ApiModelProperty(value = "冻结时间",required = true)
    @Column(name="FreezeNumber")
    private Integer freezeNumber;  // 冻结时间

    @ApiModelProperty(value = "冻结单元",required = true)
    @Column(name = "FreezeUnit")
    private String freezeUnit;     // 冻结单元，日、月、年

    @ApiModelProperty(value = "注意事件",required = true)
    @Column(name="Attentions")
    private String attentions;     // 注意事件
    
    @ApiModelProperty(value = "代投费",required = true)
    @Column(name = "Expense")
    private BigDecimal expense; // 代投费
    
    @ApiModelProperty(value = "产品基础折扣率",required = true)
    @Column(name = "BaseDiscountRate")
    private BigDecimal baseDiscountRate; // 产品基础折扣率

	@ApiModelProperty(value = "超额收益基金公司返点比例",required = true)
	@Column(name = "FoundationRate")
	private BigDecimal foundationRate; // 超额收益基金公司返点比例

    @ApiModelProperty(value = "语言类型",required = true)
    @Column(name="Lang")
    private String lang;	       // 语言类型，多个用,区分
    
    @ApiModelProperty(value = "支付币种",required = true)
    @Column(name="PaymentType")
    private String paymentType;    // 支付币种，多个用,区分

    @ApiModelProperty(value = "理财状态",required = true)
    @Column(name="Status")
    private Integer status;        // 理财状态,0-初始状态,1-募集中,2-募集结束,3-返币结束,-1-无效
    
    @ApiModelProperty(value = "年化率",required = true)
    @Column(name="AnnualRate")
    private BigDecimal annualRate; // 年化率
    
    @ApiModelProperty(value = "总收益率",required = true)
    @Column(name="TotalRate")
    private BigDecimal totalRate;  // 总收益率
    
    @ApiModelProperty(value = "理财可销售总量",required = true)
    @Column(name = "Amount")
    private BigDecimal Amount; 		   // 理财可销售总量

    @ApiModelProperty(value = "单笔最大投资限额",required = true)
    @Column(name = "CoinLimit")
    private BigDecimal coinLimit;  // 单笔投资限额
    
    @ApiModelProperty(value = "单笔最少投资限额",required = true)
    @Column(name = "coin_min_limit")
    private BigDecimal CoinMinLimit;  // 单笔投资限额
    
    @ApiModelProperty(value = "创建时间",required = false)
    @Column(name = "CreateTime")
    private Date createTime; // 记录时间

    @ApiModelProperty(value = "理财成立时期BCB价格",required = true)
    @Column(name = "CreateBcb2Usdx")
    private BigDecimal createBcb2Usdx; // 理财成立时期BCB价格

	@ApiModelProperty(value = "理财编号",required = true)
	@Column(name = "SerialNum")
	private String serialNum;  // 理财编号

    @ApiModelProperty(value = "理财成立时期BCB价格",required = true)
    @Column(name = "SubscriptionFeeRate")
    private BigDecimal subscriptionFeeRate; // 认购费优惠比例

	@ApiModelProperty(value = "理财编号",required = true)
	@Column(name = "DiscountRate")
	private BigDecimal discountRate;  // 产品基础折扣率

	@Transient
	private String financingToken; // 项目钱包地址

}
