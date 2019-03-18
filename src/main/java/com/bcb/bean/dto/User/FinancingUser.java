package com.bcb.bean.dto.User;

import lombok.Data;

@Data
public class FinancingUser {

    private String userId;   //用户id

    private String codeId;   //邀请码id

    private String displayName; //昵称

    private String step;  //用户等级

    private String financingScale;   //代理比列

    private String registTime; //注册时间

    private String checkPhonePermission;

}
