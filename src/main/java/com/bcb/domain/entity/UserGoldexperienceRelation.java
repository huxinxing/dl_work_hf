package com.bcb.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "user_goldexperience_relation")
public class UserGoldexperienceRelation {

    public static final Integer EXPERIENCE_STATUS_0 = 0;   //未激活
    public static final Integer EXPERIENCE_STATUS_1 = 1;   //计息中
    public static final Integer EXPERIENCE_STATUS_2 = 2;   //已使用
    public static final Integer EXPERIENCE_STATUS_3 = 3;   //已过期

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "id")
    private Integer id;

    @Column( name = "ge_id")
    private Integer geId;

    @Column( name = "user_id")
    private Integer userId;

    @Column( name = "financing_uuid")
    private String financingUuid;

    @Column( name = "annual_rate")
    private BigDecimal annualRate;

    @Column( name = "service_rate")
    private BigDecimal serviceRate;

    @Column( name = "receive_date")
    private Date receiveDate;

    @Column( name = "experience_begin_time")
    private Date experienceBeginTime;

    @Column( name = "experience_end_time")
    private Date experienceEndTime;

    @Column( name = "experience_status")
    private Integer experienceStatus;

    @Column( name = "ge_amount")
    private BigDecimal geAmount;

    @Column( name = "coin_name")
    private String coinName;

    @Column( name = "coin_id")
    private Integer coinId;

    @Column( name = "bcb2usdx")
    private BigDecimal bcb2Usdx;

}
