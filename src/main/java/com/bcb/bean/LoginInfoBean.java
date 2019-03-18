package com.bcb.bean;

import java.util.Date;

/**
 * Created by liq on 2018/1/16.
 */
public class LoginInfoBean {

    private Integer userId;

    private String mobileNumber;

    private Date createTime;

    public LoginInfoBean() {
    }

    public LoginInfoBean(Integer userId, String mobileNumber) {
        this.userId = userId;
        this.mobileNumber = mobileNumber;
        this.createTime = new Date();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
