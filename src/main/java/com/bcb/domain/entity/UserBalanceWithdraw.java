package com.bcb.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @ProjectName: Source
 * @Package: com.df.entity
 * @ClassName: UserAgentsFinancingWithdraw
 * @Description: java类作用描述
 * @Author: qiang wen
 * @CreateDate: 5/5/2018 6:12 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 5/5/2018 6:12 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
@Data
@Entity
@Table(name = "user_balance_withdraw")
public class UserBalanceWithdraw implements Serializable {
    private static final long serialVersionUID = 8911382223785137941L;
    // 理财提币状态
    public final static Integer FINANCING_WITHDRAW_STATUS_UNREVIEWED = 0; // 未审核
    public final static Integer FINANCING_WITHDRAW_STATUS_REVIEWED = 1; // 未确认
    public final static Integer FINANCING_WITHDRAW_STATUS_VERIFIED = 2; // 已确认
    public final static Integer FINANCING_WITHDRAW_STATUS_REJUCT = 3; //已拒绝

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "create_time")
    private Date createTime; // 记录时间

    @Column(name = "user_id")
    private Integer userId; // 用户Id

    @Column(name = "coin_id")
    private Integer coinId; // 代币类型

    @Column(name = "coin_amount")
    private BigDecimal coinAmount; // 代币金额

    @Column(name = "tx_id")
    private String txId; // 支付标识

    @Column(name = "from_address")
    private String fromAddress; // 支付标识

    @Column(name = "to_address")
    private String toAddress; // 支付标识

    @Column(name = "confirm_num")
    private Integer confirmNum; // 确认数

    @Column(name = "trade_confirm_time")
    private Date tradeConfirmTime; // 满足确认条件后的同步时间

    @Column(name = "status")
    private Integer status; // 0：待签名处理；1：已签名；2：交易已确认

    @Column(name="review_time")
    private Timestamp reviewTime;

    @Column(name="reviewer_id")
    private Integer reviewerId;

    @Column(name="handling_charge")
    private BigDecimal handlingCharge;

    @Column(name = "remark")
    private String remark;

}
