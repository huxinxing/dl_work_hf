package com.bcb.bean.dto;

import java.math.BigDecimal;

/**
 * @ProjectName: Source
 * @Package: com.bcb.bean.dto
 * @ClassName: FinancingTradeVO
 * @Description: 交易管理显示实体类
 * @Author: qiang wen
 * @CreateDate: 5/7/2018 8:03 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 5/7/2018 8:03 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class FinancingTradeVO {
    private String tradeId; // 交易流水ID
    private String projectName; // 项目名称
    private String userName; // 用户昵称
    private String fromToken; // from地址
    private String toToken; // to地址
    private String PaymentType; // 投资币种
    private BigDecimal PaymentAmount; // 投资金额
    private String confirmTime; // 确认时间
    private Integer status; // 交易状态
    private String tsId;
    private BigDecimal cnyAmount;//人民币金额
    private String financingUuid;// 项目UUID
    private String serialNum;// 项目编号

    private BigDecimal usdxAmount; // usdx金额
    private String code;// 人员编码
    private String telphone;// 手机号
    private Integer agentRebateStatus;// 返币情况
    private BigDecimal superiorRate;//额外收益上级返点比例

    private BigDecimal foundationAmount;//绩效佣金
    private BigDecimal totalAmount;// 总收益
    private BigDecimal coinAmount;// bcb金额

    private BigDecimal amount;//募集金额

    private BigDecimal serviceAmount;// 服务费
    private BigDecimal rebateAmount;//返币金额

    public BigDecimal getRebateAmount() {
        return rebateAmount;
    }

    public void setRebateAmount(BigDecimal rebateAmount) {
        this.rebateAmount = rebateAmount;
    }

    public BigDecimal getServiceAmount() {
        return serviceAmount;
    }

    public void setServiceAmount(BigDecimal serviceAmount) {
        this.serviceAmount = serviceAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCoinAmount() {
        return coinAmount;
    }

    public void setCoinAmount(BigDecimal coinAmount) {
        this.coinAmount = coinAmount;
    }

    public BigDecimal getFoundationAmount() {
        return foundationAmount;
    }

    public void setFoundationAmount(BigDecimal foundationAmount) {
        this.foundationAmount = foundationAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getSuperiorRate() {
        return superiorRate;
    }

    public void setSuperiorRate(BigDecimal superiorRate) {
        this.superiorRate = superiorRate;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public BigDecimal getUsdxAmount() {
        return usdxAmount;
    }

    public void setUsdxAmount(BigDecimal usdxAmount) {
        this.usdxAmount = usdxAmount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public Integer getAgentRebateStatus() {
        return agentRebateStatus;
    }

    public void setAgentRebateStatus(Integer agentRebateStatus) {
        this.agentRebateStatus = agentRebateStatus;
    }

    public String getFinancingUuid() {
        return financingUuid;
    }

    public void setFinancingUuid(String financingUuid) {
        this.financingUuid = financingUuid;
    }

    public BigDecimal getCnyAmount() {
        return cnyAmount;
    }

    public void setCnyAmount(BigDecimal cnyAmount) {
        this.cnyAmount = cnyAmount;
    }

    public String getTsId() {
        return tsId;
    }

    public void setTsId(String tsId) {
        this.tsId = tsId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFromToken() {
        return fromToken;
    }

    public void setFromToken(String fromToken) {
        this.fromToken = fromToken;
    }

    public String getToToken() {
        return toToken;
    }

    public void setToToken(String toToken) {
        this.toToken = toToken;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }

    public BigDecimal getPaymentAmount() {
        return PaymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        PaymentAmount = paymentAmount;
    }

    public String getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(String confirmTime) {
        this.confirmTime = confirmTime;
    }
}
