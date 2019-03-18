package com.bcb.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_balance")
public class UserBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "coin_id")
    private Integer coinId;

    @Column(name = "address")
    private String address;

    /**
     * 可用余额
     */
    @Column(name = "balance")
    private BigDecimal balance;

    /**
     * 冻结资金
     */
    @Column(name = "frozen")
    private BigDecimal frozen;

    @Version
    @Column(name = "modify_time")
    private Timestamp modifyTime;
}
