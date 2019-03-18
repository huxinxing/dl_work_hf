package com.bcb.bean.dto.OperationsManager;


import lombok.Data;

@Data
public class ConfigCommonQuestionDto {



    private String ccqId;  //ID

    private String ccqType; //常见问题类型

    private String ccqTitle;  //常见问题标题

    private String ccqQuestion;  //常见问题内容

    private String ccqWeight;  //常见问题权重

    private String ccqCreateTime;   //常见问题创建时间

}
