package com.bcb.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by kx on 2018/2/8.
 */
@Entity
@Table(name = "invest_incom_info")
public class InvestIncomInfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5796850345112772646L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id",unique = true,nullable = false)
    private Integer id;

    @Column(name = "Uuid")
    private String uuid;

    @Column(name = "UserProjectRelationId")
    private Integer userProjectRelationId;
    
    @Column(name = "UserFinancingRelationId")
    private Integer userFinancingRelationId;

    @Column(name = "TsId")
    private String tsId;

    @Column(name = "CoinType")
    private String coinType;

    @Column(name = "CoinAmount")
    private BigDecimal coinAmount;

    @Column(name = "ConfirmNum")
    private Integer confirmNum;

    @Column(name = "TradeConfirmTime")
    private Timestamp tradeConfirmTime;

    @Column(name = "Status")
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getUserProjectRelationId() {
        return userProjectRelationId;
    }

    public void setUserProjectRelationId(Integer userProjectRelationId) {
        this.userProjectRelationId = userProjectRelationId;
    }

    public Integer getUserFinancingRelationId() {
		return userFinancingRelationId;
	}

	public void setUserFinancingRelationId(Integer userFinancingRelationId) {
		this.userFinancingRelationId = userFinancingRelationId;
	}

	public String getTsId() {
        return tsId;
    }

    public void setTsId(String tsId) {
        this.tsId = tsId;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public BigDecimal getCoinAmount() {
        return coinAmount;
    }

    public void setCoinAmount(BigDecimal coinAmount) {
        this.coinAmount = coinAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getConfirmNum() {
        return confirmNum;
    }

    public void setConfirmNum(Integer confirmNum) {
        this.confirmNum = confirmNum;
    }

    public Timestamp getTradeConfirmTime() {
        return tradeConfirmTime;
    }

    public void setTradeConfirmTime(Timestamp tradeConfirmTime) {
        this.tradeConfirmTime = tradeConfirmTime;
    }
}
