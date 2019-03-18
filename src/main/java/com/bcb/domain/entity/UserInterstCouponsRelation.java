package com.bcb.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "user_interst_coupons_relation")
public class UserInterstCouponsRelation {


    public static final Integer EXPERIENCE_STATUS_0 = 0;    //未使用
    public static final Integer EXPERIENCE_STATUS_1 = 1;    //记息中
    public static final Integer EXPERIENCE_STATUS_2 = 2;    //已使用(计息结束之后的状态)
    public static final Integer EXPERIENCE_STATUS_3 = 3;    //已过期

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "id")
    private Integer id;

    @Column( name = "ic_id")
    private Integer icId;

    @Column( name = "user_id")
    private Integer userId;

    @Column( name = "record_id")
    private Integer recordId;

    @Column( name = "annual_rate")
    private BigDecimal annualRate;

    @Column( name = "servicel_rate")
    private BigDecimal servicelRate;

    @Column( name = "receive_date")
    private Date receiveDate;

    @Column( name = "ic_begin_time")
    private Date icBeginTime;

    @Column( name = "ic_end_time")
    private Date icEndTime;

    @Column( name = "ic_status")
    private Integer icStatus;

    @Column( name = "ic_seletment")
    private BigDecimal icSeletment;

    @Column( name = "coin_name")
    private String coinName;

    @Column( name = "coin_id")
    private Integer coinId;

    @Column( name = "bcb2usdx")
    private BigDecimal bcb2Usdx;

}
