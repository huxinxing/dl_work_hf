package com.bcb.bean.dto.OperationsManager;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GoldExperienceListDto {

    private int geId;

    private BigDecimal geAmount;  //体验金额度 单位usdx

    private String condition;

    private int experienceLength;  //体验时长

    private List<String> experienceProject;   //体验项目   map key:项目uuid   value：项目名称

    private String validityTime;  // 有效期


}
