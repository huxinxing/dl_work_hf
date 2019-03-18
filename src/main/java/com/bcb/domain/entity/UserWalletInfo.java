package com.bcb.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "user_wallet_info")
public class UserWalletInfo implements Serializable{

    public static final Integer WALLET_STATUS_INVALID = 0;
    public static final Integer WALLET_STATUS_VALID = 1;
    public static final Integer WALLET_STATUS_VERIFIED = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id",unique = true,nullable = false)
    private Integer id;

    @Column(name = "CreateTime")
    private Timestamp createTime;

    @Column(name = "UserId")
    private Integer userId;

    @Column(name = "Type")
    private Integer type;

    @Column(name = "Token")
    private String token;

    @Column(name = "Status")
    private Integer status;

}
