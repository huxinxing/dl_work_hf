package com.bcb.bean.dto.OperationsManager;

import lombok.Data;

@Data
public class HomeConfigGeDto {

    private String geId;  //体验金id

    private String geAmount;  //体验金额

    private String geStatus;   //体验金状态

    private String status;   //是否上架

}
