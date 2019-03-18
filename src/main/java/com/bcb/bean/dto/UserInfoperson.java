package com.bcb.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


public class UserInfoperson  implements Serializable {
    private static final long serialVersionUID = -2207344252173230075L;

    private Integer userId;
    private String displayName; // 显示昵称
    private String createTime; // 记录时间
    private Integer agentLevel;
    private BigDecimal Scale; // 上级代理提取的分成比例
    private String code;  //推荐码
    List<String> listWallet;
    private String countryCode;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getAgentLevel() {
        return agentLevel;
    }

    public void setAgentLevel(Integer agentLevel) {
        this.agentLevel = agentLevel;
    }

    public BigDecimal getScale() {
        return Scale;
    }

    public void setScale(BigDecimal scale) {
        Scale = scale;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getListWallet() {
        return listWallet;
    }

    public void setListWallet(List<String> listWallet) {
        this.listWallet = listWallet;
    }
}
