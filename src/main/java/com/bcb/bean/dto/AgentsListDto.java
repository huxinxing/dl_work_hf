package com.bcb.bean.dto;

import java.math.BigDecimal;

/**
 * 代理返利列表
 * Created by liq on 2018/1/28.
 */
public class AgentsListDto {

    private Integer agentsId; // 下级代理Id

    private String displayName; // 代理昵称

    private BigDecimal coinAmount; // 代币金额

    private BigDecimal amount; // 投资ETH金额

    public Integer getAgentsId() {
        return agentsId;
    }

    public void setAgentsId(Integer agentsId) {
        this.agentsId = agentsId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public BigDecimal getCoinAmount() {
        return coinAmount;
    }

    public void setCoinAmount(BigDecimal coinAmount) {
        this.coinAmount = coinAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
