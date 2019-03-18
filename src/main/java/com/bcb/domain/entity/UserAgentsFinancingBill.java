package com.bcb.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "user_agents_financing_bill")
public class UserAgentsFinancingBill implements Serializable {
    private static final long serialVersionUID = -2836756077367825501L;
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "create_time")
    private Date createTime; // 记录时间

    @Column(name = "record_id")
    private Integer recordId; // 投资表主键

    @Column(name = "user_id")
    private Integer userId; // 用户Id

    private BigDecimal amount; // 代币金额

    @Column(name = "tax")
    private  BigDecimal tax;   //代理税费

}
