package com.bcb.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_financing_record")
public class UserFinancingRecord {

    public static final Integer RECORD_STATUS_UNCONFIRMED = 1;   //待确认
    public static final Integer RECORD_STATUS_ONGOING = 2;   //进行中
    public static final Integer RECORD_STATUS_OVER = 3;   //理财结束
    public static final Integer RECORD_STATUS_SETTLEMENT_OVER = 4; //结算完成
    public static final  Integer RECORD_STATUS_INVALID = -1;   //参与失败

    public static final Integer AGENTS_STATUS_SUCCESS = 1; //返点成功
    public static final Integer AGENTS_STATUS_FAIL = 0; //返点失败

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column( name = "id")
    private Integer id;

    @Column(name = "record_create_time")
    private Timestamp recordCreateTime;

    @Column(name = "block_create_time")
    private Timestamp blockCreateTime;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "financing_uuid")
    private String financingUuid;

    @Column(name = "coin_id")
    private Integer coinId;

    @Column(name = "coin_name")
    private String coinName;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "to_address")
    private String toAddress;

    @Column(name = "tx_id")
    private String txId;

    @Column(name = "block_num")
    private Long blockNum;

    @Column(name = "bcb_amount")
    private BigDecimal bcbAmount;

    @Column(name = "usdx_amount")
    private BigDecimal usdxAmount;

    @Column(name = "confirm_num")
    private Integer confirmNum;

    @Column(name = "record_status")
    private Integer recordStatus;

    @Column(name = "agents_status")
    private Integer agentsStatus;

    @Column(name = "bcb2usdx")
    private BigDecimal bcb2usdx;

    @Column(name = "coin2bcb")
    private BigDecimal coin2bcb;

    @Column(name = "annual_rate")
    private BigDecimal annualRate;

    @Column(name = "service_rate")
    private BigDecimal serviceRate;

    @Column(name = "foundation_commission_rate")
    private BigDecimal foundationCommissionRate;

    @Column(name = "superior_commission_rate")
    private BigDecimal superiorCommissionRate;

    @Column(name = "user_discount_rate")
    private BigDecimal userDiscountRate;

    @Column(name = "financing_discount_rate")
    private BigDecimal financingDiscountRate;

    @Column(name = "subscription_fee")
    private BigDecimal subscriptionFee;

    @Column(name = "subscription_fee_rate")
    private BigDecimal subscriptionFeeRate;

    @Column(name = "redeemption_status")
    private Integer redeemptionStatus;

    @Column(name =  "redeemption_time")
    private Date redeemptionTime;

    @Column(name =  "fail_order_status")
    private Integer failOrderStatus;

    @Column(name = "expire_time")
    private Timestamp expireTime;

    @Version
    @Column(name = "modify_time")
    private Timestamp modifyTime;
}
