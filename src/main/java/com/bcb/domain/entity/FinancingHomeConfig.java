package com.bcb.domain.entity;


import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "financing_home_config")
public class FinancingHomeConfig {

    public static Integer FINANCING_HOME_CONFIG_TYPE_ONE = 1;  //首页配置类型1 轮播图
    public static Integer FINANCING_HOME_CONFIG_TYPE_TWO = 2;  //首页配置类型2 体验金
    public static Integer FINANCING_HOME_CONFIG_TYPE_THREE = 3;  //首页配置类型3 新人见面礼图片

    public static Integer FINANCING_HOME_CONFIG_STATUS_ONE = 1;  //配置首页类型1  已上架
    public static Integer FINANCING_HOME_CONFIG_STATUS_TWO = 2;  //首页配置类型2  已下架

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "hc_id")
    private Integer hcId;

    @Column( name = "type")
    private Integer type;

    @Column( name = "type_id")
    private Integer typeId;

    @Column( name = "validity_begin_time")
    private Timestamp validityBeginTime;

    @Column( name = "validity_end_time")
    private Timestamp validityEndTime;

    @Column( name = "device_type")
    private String deviceType;

    @Column( name = "weight")
    private Integer weight;

    @Column( name = "status")
    private Integer status;

    @Column( name = "text_describe")
    private String textDescribe;

    @Column( name = "hc_modify_time")
    private Timestamp hcModifyTime;

}
