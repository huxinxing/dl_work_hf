package com.bcb.domain.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "system_log_operation")
public class SystemLogOperation implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id",unique = true,nullable = false)
    private Integer id;

    @ApiModelProperty(value = "请求请求的URL",required = false)
    @Column(name = "RequestUrl")
    private String requestUrl;

    @ApiModelProperty(value = "请求方法",required = false)
    @Column(name = "HttpMethod")
    private String httpMethod;


    @ApiModelProperty(value = "操作的IP",required = false)
    @Column(name = "Ip")
    private String ip;

    @ApiModelProperty(value = "类名",required = true)
    @Column(name = "ClassName")
    private String className;


    @ApiModelProperty(value = "方法名称",required = true)
    @Column(name = "MethodName")
    private String methodName;

    @ApiModelProperty(value = "请求的参数内容",required = false)
    @Column(name = "ParameterContext")
    private String parameterContext;


    @ApiModelProperty(value = "操作人员",required = true)
    @Column(name = "OperatorName")
    private String operatorName;


    @ApiModelProperty(value = "创建时间",required = true)
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Column(name = "CreateTime")
    private Date createTime;



    @ApiModelProperty(value = "操作类型",required = false)
    @Column(name = "ActionType")
    private String actionType;


    @ApiModelProperty(value = "操作人员",required = true)
    @Column(name = "CommentDescribe")
    private String commentDescribe;
}
