package com.bcb.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by kx on 2018/1/16.
 */
@Entity
@Table(name = "user_account_info")
public class UserAccountInfo implements Serializable{
    private static final long serialVersionUID = -2207344252173230075L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId", unique = true, nullable = false)
    private Integer userId;

    @Column(name = "CreateTime")
    private Timestamp createTime; // 记录时间

    @Column(name = "MobileNumber")
    private String mobileNumber; // 手机号

    @Column(name = "CountryCode")
    private String countryCode; // 国码

    @Column(name = "DisplayName")
    private String displayName; // 显示昵称

    @Column(name = "Status")
    private Integer status; // 1：生效；0：失效

    @Column(name = "ParentId")
    private Integer parentId; // 上级代理Id
    
    @Column(name = "FinancingScale")
    private BigDecimal financingScale; // 上级代理提取的理财分成比例

    @Column(name = "SecurityPassword")
    private String securityPassword; // 资金密码

    @Column(name = "Salt")
    private String salt; // 密码盐

    @Column(name = "UpdatePwdTime")
    private Date updatePwdTime; // 修改密码时间

    @Column(name = "UserDiscountRate")
    private BigDecimal userDiscountRate; //用户折扣比率

    @Transient
    private String cTime;
    @Transient
    private Integer agentLevel;
    @Transient
    private String code;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public BigDecimal getFinancingScale() {
		return financingScale;
	}

	public void setFinancingScale(BigDecimal financingScale) {
		this.financingScale = financingScale;
	}

	public String getSecurityPassword() {
        return securityPassword;
    }

    public void setSecurityPassword(String securityPassword) {
        this.securityPassword = securityPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Date getUpdatePwdTime() {
        return updatePwdTime;
    }

    public void setUpdatePwdTime(Date updatePwdTime) {
        this.updatePwdTime = updatePwdTime;
    }

    public String getcTime() {
        return cTime;
    }

    public void setcTime(String cTime) {
        this.cTime = cTime;
    }

    public Integer getAgentLevel() {
        return agentLevel;
    }

    public void setAgentLevel(Integer agentLevel) {
        this.agentLevel = agentLevel;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getUserDiscountRate() {
        return userDiscountRate;
    }

    public void setUserDiscountRate(BigDecimal userDiscountRate) {
        this.userDiscountRate = userDiscountRate;
    }
}
