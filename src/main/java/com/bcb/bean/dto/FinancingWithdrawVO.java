package com.bcb.bean.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FinancingWithdrawVO {
    private Integer id; // 提币记录ID
    private String createTime; // 提币记录操作时间
    private Integer userId; // 用户ID
    private String coinName; // 代币类型
    private BigDecimal coinAmount; // 提币额度
    private String tsId;  // 提币记录标识ID
    private Integer ConfirmNum; // 确认次数
    private String  TradeConfirmTime; // 确认时间
    private Integer status; // 状态
    private String reviewTime; // 审核时间
    private Integer reviewerId; // 审核人ID
    private String userName; // 用户名
    private String reviewerName; // 审核人名称

}
