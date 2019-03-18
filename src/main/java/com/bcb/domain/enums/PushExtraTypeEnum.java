package com.bcb.domain.enums;

import java.util.HashMap;
import java.util.Map;


public enum PushExtraTypeEnum implements IBaseEnum {
    URL(1, "H5链接"),
    ORDER_SUCCESS(2, "订单成功"),
    ORDER_COMPLETING(3, "订单将到期");

    private Integer value;
    private String display;
    private static Map<Integer, PushExtraTypeEnum> valueMap = new HashMap<>();

    static {
        for (PushExtraTypeEnum e : PushExtraTypeEnum.values()) {
            valueMap.put(e.value, e);
        }
    }

    PushExtraTypeEnum(Integer value, String display) {
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

    public static PushExtraTypeEnum getByValue(Integer value) {
        return valueMap.get(value);
    }
}
