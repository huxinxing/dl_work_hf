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
@Table(name = "user_transfer")
public class UserTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "id")
    private Integer id = 0;

    @Column(name = "record_id")
    private Integer recordId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "coin_id")
    private Integer coinId;

    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "to_address")
    private String toAddress;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "fee")
    private BigDecimal fee;

    /**
     * 可用余额
     */
    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "modify_time")
    private Timestamp modifyTime;

    @Column(name = "remark")
    private String remark;

    @Column(name = "type")
    private Integer type;
}
