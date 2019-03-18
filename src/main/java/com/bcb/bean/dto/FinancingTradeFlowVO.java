package com.bcb.bean.dto;

import java.math.BigDecimal;

/**
 * @ProjectName: Source
 * @Package: com.bcb.bean.dto
 * @ClassName: FinancingTradeVO
 * @Description: 交易流水显示实体类
 * @Author: qiang wen
 * @CreateDate: 5/7/2018 8:03 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 5/7/2018 8:03 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class FinancingTradeFlowVO {
    private Integer agentBillId; // 代理流水ID
    private Integer tradeId; // 交易ID
    private String userId; // 用户ID
    private String userName; // 用户名
    private BigDecimal paymentAmount; // 投资金额
    private String paymentType; // 投资币种
    private BigDecimal investAmount; // 收益金额
    private String investCoinType; // 收益结算币种
    private BigDecimal agentRateAmount; // 返利
    private String agentCoinType; // 返利结算币种
    private String createTime; // 结算时间
    private BigDecimal superiorRate;//额外收益上级返点比例
    private BigDecimal totalRate;// 固定年华收益率
    private String financingUuid;// 项目UUID
    private String title;// 理财名称
    private String serialNum;// 理财编号

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getFinancingUuid() {
        return financingUuid;
    }

    public void setFinancingUuid(String financingUuid) {
        this.financingUuid = financingUuid;
    }

    public BigDecimal getSuperiorRate() {
        return superiorRate;
    }

    public void setSuperiorRate(BigDecimal superiorRate) {
        this.superiorRate = superiorRate;
    }

    public BigDecimal getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(BigDecimal totalRate) {
        this.totalRate = totalRate;
    }

    private String startTime;
    private String endTime;
    private String walletAddr;//钱包地址

    public String getWalletAddr() {
        return walletAddr;
    }

    public void setWalletAddr(String walletAddr) {
        this.walletAddr = walletAddr;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

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

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getInvestAmount() {
        return investAmount;
    }

    public void setInvestAmount(BigDecimal investAmount) {
        this.investAmount = investAmount;
    }

    public String getInvestCoinType() {
        return investCoinType;
    }

    public void setInvestCoinType(String investCoinType) {
        this.investCoinType = investCoinType;
    }

    public BigDecimal getAgentRateAmount() {
        return agentRateAmount;
    }

    public void setAgentRateAmount(BigDecimal agentRateAmount) {
        this.agentRateAmount = agentRateAmount;
    }

    public String getAgentCoinType() {
        return agentCoinType;
    }

    public void setAgentCoinType(String agentCoinType) {
        this.agentCoinType = agentCoinType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
