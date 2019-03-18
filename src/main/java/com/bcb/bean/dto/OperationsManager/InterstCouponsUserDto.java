package com.bcb.bean.dto.OperationsManager;

import lombok.Data;

@Data
public class InterstCouponsUserDto {

    private int userid;   //用户id

    private String displayName;   //用户昵称

    private int geId;   //体验金id

    private String reviceTime;  //体验金领取时间

    private String geState;   //体验金状态

}
