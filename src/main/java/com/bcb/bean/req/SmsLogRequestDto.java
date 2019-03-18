package com.bcb.bean.req;

public class SmsLogRequestDto {
    private String mobilePhone;//手机号码
    private String isdealWith;//是否处理
    private String isSend;//是否发送
    private String createTimeStart;//开始时间
    private String createTimeEnd;//结束时间
    private String smsChannel;


    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getIsdealWith() {
        return isdealWith;
    }

    public void setIsdealWith(String isdealWith) {
        this.isdealWith = isdealWith;
    }

    public String getIsSend() {
        return isSend;
    }

    public void setIsSend(String isSend) {
        this.isSend = isSend;
    }

    public String getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(String createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public String getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public String getSmsChannel() {
        return smsChannel;
    }

    public void setSmsChannel(String smsChannel) {
        this.smsChannel = smsChannel;
    }
}
