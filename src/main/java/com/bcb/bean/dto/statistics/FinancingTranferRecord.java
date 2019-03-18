package com.bcb.bean.dto.statistics;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FinancingTranferRecord {

    public String id;

    public String type;

    public String userId;

    public String userName;

    public String fromAddress;

    public String toAddress;

    public String coinType;

    public String amount;

    public String TransferTime;

}
