package com.bcb.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "financing_wallet_info")
public class FinancingWalletInfo implements Serializable {
	private static final long serialVersionUID = 1064792812673627238L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id",unique = true,nullable = false)
    private Integer id;

    @Column(name = "CreateTime")
    private Date createTime;//创建时间

    @Column(name = "FinancingUuid")
    private String financingUuid;//项目唯一标示

    @Column(name = "WalletType")
    private Integer walletType;//钱包类型

    @Column(name = "WalletLevel")
    private Integer walletLevel;//钱包级别

    @Column(name = "Token")
    private String token;//钱包地址

    @Column(name = "Status")
    private Integer status;//状态（1：生效；0：失效）

    @Column(name = "FileByte")
    private byte[] fileByte;//导入项目地址签名文件

    @Column(name = "TokenIndex")
    private String tokenIndex;//钱包地址索引号

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getFinancingUuid() {
        return financingUuid;
    }

    public void setFinancingUuid(String financingUuid) {
        this.financingUuid = financingUuid;
    }

    public Integer getWalletType() {
        return walletType;
    }

    public void setWalletType(Integer walletType) {
        this.walletType = walletType;
    }

    public Integer getWalletLevel() {
        return walletLevel;
    }

    public void setWalletLevel(Integer walletLevel) {
        this.walletLevel = walletLevel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public byte[] getFileByte() {
        return fileByte;
    }

    public void setFileByte(byte[] fileByte) {
        this.fileByte = fileByte;
    }

    public String getTokenIndex() {
        return tokenIndex;
    }

    public void setTokenIndex(String tokenIndex) {
        this.tokenIndex = tokenIndex;
    }
}
