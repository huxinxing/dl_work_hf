package com.bcb.domain.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "system_log_sms")
public class SystemLogSms implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id",unique = true,nullable = false)
    private Integer id;


    @ApiModelProperty(value = "创建时间",required = true)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Column(name = "CreateTime")
    private Date createTime;

    @ApiModelProperty(value = "国内手机同，国外手机",required = false)
    @Column(name = "MobileType")
    private String mobileType;


    @ApiModelProperty(value = "1、末处理 2已处理（准备发送短信验证码的流程）",required = false)
    @Column(name = "IsdealWith")
    private String isdealWith;

    @ApiModelProperty(value = "是否发送.1、已送",required = false)
    @Column(name = "IsSend")
    private String isSend;


    @ApiModelProperty(value = "国家编码",required = false)
    @Column(name = "CountryCode")
    private String countryCode;

    @ApiModelProperty(value = "手机",required = false)
    @Column(name = "MobilePhone")
    private String  mobilePhone;


    @ApiModelProperty(value = "短信内容",required = false)
    @Column(name = "SmsContext")
    private String smsContext;

    @ApiModelProperty(value = "请求内容",required = false)
    @Column(name = "RequestContext")
    private String RequestContext;

    @ApiModelProperty(value = "返回结果",required = false)
    @Column(name = "ResultContext")
    private String resultContext;

    @ApiModelProperty(value = "创建时间",required = true)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Column(name = "UpdateTime")
    private Date updateTime;

    @ApiModelProperty(value = "返回结果",required = false)
    @Column(name = "SmsCode")
    private String smsCode;

    @Column(name = "SmsChannel")
    private String smsChannel;
}
