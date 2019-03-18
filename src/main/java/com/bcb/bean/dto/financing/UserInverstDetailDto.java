package com.bcb.bean.dto.financing;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Pattern;

public class UserInverstDetailDto {

    private String title;

    private String coinType;

    private BigDecimal coinAmount;

    private String usdxCoinType = "USDX";

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

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getUsdxCoinType() {
        return usdxCoinType;
    }

    public void setUsdxCoinType(String usdxCoinType) {
        this.usdxCoinType = usdxCoinType;
    }
}
