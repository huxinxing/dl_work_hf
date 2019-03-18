package com.bcb.bean.dto.OperationsManager;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class GoldExperienceList {

    private int geId;

    private BigDecimal geAmount;  //体验金额度 单位usdx

    private int experienceLength;  //体验时长

    private List<String> experienceProject;   //体验项目   map key:项目uuid   value：项目名称

    private String condition;   //满足条件

    private String validityTime;  // 有效期

}
