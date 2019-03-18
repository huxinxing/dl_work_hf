package com.bcb.domain.entity;

import io.swagger.models.auth.In;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name = "financing_activity_goldexperience")
public class FinancingActivityGoldexperience {

    public static final String GE_STATUS_0 = "已关闭";
    public static final String GE_STATUS_1 = "已开启";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "ge_id")
    private Integer geId;

    @Column( name = "ge_amount")
    private BigDecimal geAmount;

    @Column( name = "experience_length")
    private Integer experienceLength;

    @Column( name = "experience_project")
    private String experienceProject;

    @Column( name = "ge_condition")
    private String geCondition;

    @Column( name = "validity_day")
    private Integer validityDay;

    @Column( name = "validity_begin_time")
    private Date validityBeginTime;

    @Column( name = "validity_end_time")
    private Date validityEndTime;

    @Column( name = "ge_status")
    private Integer geStatus;

    @Column(name = "ge_create_time")
    private Date geCreateTime;


}
