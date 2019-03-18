package com.bcb.domain.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by liq on 2018/3/20.
 */
@Data
@Entity
@Table(name = "block_operation_info")
public class BlockOperationInfo implements Serializable {

    private static final long serialVersionUID = -3763579790409867077L;

    public static final Integer BLOCK_OPERATION_INFO_STATUS_ONE = 0;  //操作登记初始状态

    public static final Integer BLOCK_OPERATION_INFO_STATUS_TWO = 1;  //已操作打币

    public static final Integer BLOCK_OPERATION_INFO_STATUS_THREE = 2; //打币结果已确认


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "CreateTime")
    private Date createTime;

    @Column(name = "BusinessId")
    private String businessId;

    @Column(name = "Statue")
    private Integer statue;

    @Column(name = "CoinType")
    private String coinType;

    @Column(name = "FromToken")
    private String fromToken;

    @Column(name = "ToToken")
    private String toToken;

    @Column(name = "Amount")
    private BigDecimal amount;

    @Column(name = "TxId")
    private String txId;

    @Column(name = "ConfirmNum")
    private Integer confirmNum;

    @Column(name = "TradeConfirmTime")
    private Date tradeConfirmTime;

}
