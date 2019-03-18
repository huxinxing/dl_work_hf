package com.bcb.bean.dto.OperationsManager;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class GoldExperienceDto {

    private int geId;

    private BigDecimal geAmount;  //体验金额度 单位usdx

    private Integer experienceLength;  //   体验时长

    private Map<String,Object> experienceProject;   //体验项目   map key:项目uuid   value：项目名称

    private BigDecimal condition;   //满足条件

    private String validityDay;  //有效天数

    private String validityBeginTime; //有效开始时间

    private String validityEndTime;  //有效结束时间

    private Integer geStatus;  //体验金状态

    private String geCreateTime; //体验金创建时间

}
