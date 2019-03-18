package com.bcb.bean.dto.financing;

import java.math.BigDecimal;

/**
 * 用户投资统计结果
 */
public class UserInverstStatisticsResponse {

    private String title;
    private BigDecimal coinAmount;
    private BigDecimal usdxAmount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getCoinAmount() {
        return coinAmount;
    }

    public void setCoinAmount(BigDecimal coinAmount) {
        this.coinAmount = coinAmount;
    }

    public BigDecimal getUsdxAmount() {
        return usdxAmount;
    }

    public void setUsdxAmount(BigDecimal usdxAmount) {
        this.usdxAmount = usdxAmount;
    }
}
