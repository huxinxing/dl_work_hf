package com.bcb.domain.enums;

import java.util.HashMap;
import java.util.Map;

public enum UserFinancingRecordStatusEnum implements IBaseEnum {
    待确认(1, "待确认"),
    进行中(2, "进行中"),
    理财结束(3, "理财结束"),
    结算完成(4, "结算完成"),
    参与失败(-1, "参与失败");


    private Integer value;
    private String display;
    private static Map<Integer, UserFinancingRecordStatusEnum> valueMap = new HashMap<>();

    static {
        for (UserFinancingRecordStatusEnum e : UserFinancingRecordStatusEnum.values()) {
            valueMap.put(e.value, e);
        }
    }

    UserFinancingRecordStatusEnum(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String getDisplay() {
        return this.display;
    }

    public static UserFinancingRecordStatusEnum getByValue(Integer value) {
        return valueMap.get(value);
    }
}
