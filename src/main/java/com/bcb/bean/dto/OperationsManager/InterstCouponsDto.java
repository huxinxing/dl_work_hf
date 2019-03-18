package com.bcb.bean.dto.OperationsManager;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class InterstCouponsDto {

    public Integer icId;   //加息券id

    public BigDecimal icRate;    //加息利率

    public Integer icLength;   //加息时长

    private Map<String,Object> icProject;   //加息券可用项目

    public String icValidityDay;  //加息券体验天数

    public String icValidityBeginTime;   //有效起始时间

    public String icValidityEndTime;   //有效结束时间

    public  Integer icStatus;  //体验金状态 0：可领取  1：不可领取

    public String icCreateTime;  // 体验金创建时间
    
}
