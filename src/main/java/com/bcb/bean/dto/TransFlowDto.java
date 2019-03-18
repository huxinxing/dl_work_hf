package com.bcb.bean.dto;

import java.math.BigDecimal;

public class TransFlowDto {
    private Integer agentBillId;
    private Integer tradeId;
    private String userId;
    private String userName;
    private BigDecimal amount;
    private BigDecimal investAmount;
    private String investCoinType;
    private BigDecimal agentRateAmount;
    private String agentCoinType;
    private String createTime;
    private String paymentType;
    private String usdxAmount;

    public Integer getAgentBillId() {
        return agentBillId;
    }

    public void setAgentBillId(Integer agentBillId) {
        this.agentBillId = agentBillId;
    }

    public Integer getTradeId() {
        return tradeId;
    }

    public void setTradeId(Integer tradeId) {
        this.tradeId = tradeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getInvestAmount() {
        return investAmount;
    }

    public void setInvestAmount(BigDecimal investAmount) {
        this.investAmount = investAmount;
    }

    public BigDecimal getAgentRateAmount() {
        return agentRateAmount;
    }

    public void setAgentRateAmount(BigDecimal agentRateAmount) {
        this.agentRateAmount = agentRateAmount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getInvestCoinType() {
        return investCoinType;
    }

    public void setInvestCoinType(String investCoinType) {
        this.investCoinType = investCoinType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getUsdxAmount() {
        return usdxAmount;
    }

    public void setUsdxAmount(String usdxAmount) {
        this.usdxAmount = usdxAmount;
    }

    public String getAgentCoinType() {
        return agentCoinType;
    }

    public void setAgentCoinType(String agentCoinType) {
        this.agentCoinType = agentCoinType;
    }


}
