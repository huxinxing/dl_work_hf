package com.bcb.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserInfochild implements Serializable {
    private static final long serialVersionUID = -2207344252173230075L;

    private Integer userId;
    private String displayName; // 显示昵称
    private String code;  //推荐码
    private BigDecimal investTatolSelf;
    private BigDecimal investTatolLevel;
    private BigDecimal agentsTatolSelf;
    private BigDecimal agentsTatolLevel;
    private BigDecimal investUsdxTatolSelf;
    private BigDecimal investUsdxTatolLevel;
    private String paymentType;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getInvestTatolSelf() {
        return investTatolSelf;
    }

    public void setInvestTatolSelf(BigDecimal investTatolSelf) {
        this.investTatolSelf = investTatolSelf;
    }

    public BigDecimal getInvestTatolLevel() {
        return investTatolLevel;
    }

    public void setInvestTatolLevel(BigDecimal investTatolLevel) {
        this.investTatolLevel = investTatolLevel;
    }

    public BigDecimal getAgentsTatolSelf() {
        return agentsTatolSelf;
    }

    public void setAgentsTatolSelf(BigDecimal agentsTatolSelf) {
        this.agentsTatolSelf = agentsTatolSelf;
    }

    public BigDecimal getAgentsTatolLevel() {
        return agentsTatolLevel;
    }

    public void setAgentsTatolLevel(BigDecimal agentsTatolLevel) {
        this.agentsTatolLevel = agentsTatolLevel;
    }

    public BigDecimal getInvestUsdxTatolSelf() {
        return investUsdxTatolSelf;
    }

    public void setInvestUsdxTatolSelf(BigDecimal investUsdxTatolSelf) {
        this.investUsdxTatolSelf = investUsdxTatolSelf;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getInvestUsdxTatolLevel() {
        return investUsdxTatolLevel;
    }

    public void setInvestUsdxTatolLevel(BigDecimal investUsdxTatolLevel) {
        this.investUsdxTatolLevel = investUsdxTatolLevel;
    }
}
