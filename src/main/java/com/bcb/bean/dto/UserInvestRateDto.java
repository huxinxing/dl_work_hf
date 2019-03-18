package com.bcb.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserInvestRateDto implements Serializable{
    private Integer investId;
    private Integer agentId;
    private Integer tradeId;
    private String userName;
    private BigDecimal scale;
    private BigDecimal ethAmount;
    private BigDecimal bcbAmount;
    private String bcbAgentAmount;
    private String paymentType;//投资方式
    private String paymentAmount;// 投资金额
    private BigDecimal usdxAmount;
    private BigDecimal financingScale;//理财返点比例

    public BigDecimal getFinancingScale() {
        return financingScale;
    }

    public void setFinancingScale(BigDecimal financingScale) {
        this.financingScale = financingScale;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getUsdxAmount() {
        return usdxAmount;
    }

    public void setUsdxAmount(BigDecimal usdxAmount) {
        this.usdxAmount = usdxAmount;
    }

    private String investName;

    private String code;
    private String agentLevel;

    public Integer getTradeId() {
        return tradeId;
    }

    public void setTradeId(Integer tradeId) {
        this.tradeId = tradeId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getScale() {
        return scale;
    }

    public void setScale(BigDecimal scale) {
        this.scale = scale;
    }

    public BigDecimal getEthAmount() {
        return ethAmount;
    }

    public void setEthAmount(BigDecimal ethAmount) {
        this.ethAmount = ethAmount;
    }

    public BigDecimal getBcbAmount() {
        return bcbAmount;
    }

    public void setBcbAmount(BigDecimal bcbAmount) {
        this.bcbAmount = bcbAmount;
    }

    public String getBcbAgentAmount() {
        return bcbAgentAmount;
    }

    public void setBcbAgentAmount(String bcbAgentAmount) {
        this.bcbAgentAmount = bcbAgentAmount;
    }

    public Integer getInvestId() {
        return investId;
    }

    public void setInvestId(Integer investId) {
        this.investId = investId;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getInvestName() {
        return investName;
    }

    public void setInvestName(String investName) {
        this.investName = investName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAgentLevel() {
        return agentLevel;
    }

    public void setAgentLevel(String agentLevel) {
        this.agentLevel = agentLevel;
    }
}
