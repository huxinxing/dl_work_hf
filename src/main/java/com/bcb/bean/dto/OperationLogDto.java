package com.bcb.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
public class OperationLogDto implements Serializable {
    private Integer id;

    private String requestUrl;

    private String httpMethod;

    private String ip;

    private String className;

    private String methodName;

    private String parameterContext;

    private String operatorName;


    private String createTime;

    private String actionType;


    private String commentDescribe;


}

