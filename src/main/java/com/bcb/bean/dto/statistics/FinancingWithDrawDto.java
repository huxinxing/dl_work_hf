package com.bcb.bean.dto.statistics;

import lombok.Data;

@Data
public class FinancingWithDrawDto {

    public String id;  //提币id

    public String userId;  //用户id

    public String userName;  //用户昵称

    public String coinName;  //提币币种

    public String amount; //提币金额

    public String amountBalance; //账户余额

    public String address;  //提币地址

    public String createTime;  //订单创建时间

    public String confirmType;   //确认状态

    public String reviewTime;   //审核时间

    public String reviewer;   //审核员

    public String remark;  //备注：用于审核拒绝，拒绝理由


}
