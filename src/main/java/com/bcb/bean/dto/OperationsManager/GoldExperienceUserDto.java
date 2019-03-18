package com.bcb.bean.dto.OperationsManager;

import lombok.Data;

import java.util.Date;

@Data
public class GoldExperienceUserDto {

    private int userid;   //用户id

    private String displayName;   //用户昵称

    private int geId;   //体验金id

    private String reviceTime;  //体验金领取时间

    private String geState;   //体验金状态

}
