package com.bcb.bean.dto;

/**
 * Created by kx on 2018/1/24.
 */
public class AccountDto {
    private Integer role;

    private String token;

    private String loginName;

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
