package com.bcb.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name = "financing_activity_interst_coupons")
public class FinancingActivityInterstCoupons {

    public static final String IC_STATUS_0 = "已关闭";
    public static final String IC_STATUS_1 = "已开启";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "ic_id")
    private Integer icId;

    @Column( name = "ic_rate")
    private BigDecimal icRate;

    @Column( name = "ic_length")
    private Integer icLength;

    @Column( name = "ic_project")
    private String icProject;

    @Column( name = "ic_validity_day")
    private Integer icValidityDay;

    @Column( name = "ic_validity_begin_time")
    private Date icValidityBeginTime;

    @Column( name = "ic_validity_end_time")
    private Date icValidityEndTime;

    @Column( name = "ic_status")
    private Integer icStatus;

    @Column( name = "ic_create_time")
    private Date icCreateTime;


}
