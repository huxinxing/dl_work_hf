package com.bcb.bean.dto.OperationsManager;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class InterstCouponsListDto {


    private int icId;

    private BigDecimal icRate;  //体验金额度 单位usdx

    private int icLength;  //体验时长

    private List<String> icProject;   //体验项目   map key:项目uuid   value：项目名称

    private String validityTime;  // 有效期

}
