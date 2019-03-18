package com.bcb.domain.entity;

import lombok.Data;
import org.omg.CORBA.INTERNAL;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "financing_config_common_question")
public class FinancingConfigCommonQuestion {

    public static Integer CONFIG_COMMON_QUESTION_ONE = 1;  //常见问题配置 1 登录注册:
    public static Integer CONFIG_COMMON_QUESTION_TWO = 2;  //常见问题配置 2 账户安全:
    public static Integer CONFIG_COMMON_QUESTION_THREE = 3;  //常见问题配置 3 充值投资:
    public static Integer CONFIG_COMMON_QUESTION_FOUR = 4;  //常见问题配置 4 提现 :
    public static Integer CONFIG_COMMON_QUESTION_FIVE = 5;  //常见问题配置 5 费用:
    public static Integer CONFIG_COMMON_QUESTION_SIX = 6;  //常见问题配置 6 资产:

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "ccq_id")
    private Integer ccqId;

    @Column( name = "ccq_type")
    private Integer ccqType;

    @Column( name = "ccq_title")
    private String ccqTitle;

    @Column( name = "ccq_question")
    private String ccqQuestion;

    @Column( name = "ccq_weight")
    private Integer ccqWeight;

    @Column( name = "ccq_lang")
    private String ccqLang;

    @Column( name = "ccq_lang_ascription")
    private Integer ccqLangAscription;

    @Column( name = "ccq_create_time")
    private Timestamp ccqCreateTime;

}
