package com.bcb.domain.enums;

import java.util.HashMap;
import java.util.Map;

public enum SystemButtonMenuStatusEnum implements IBaseEnum {
    不显示(0, "不显示"),
    显示(1, "显示");


    private Integer value;
    private String display;
    private static Map<Integer, SystemButtonMenuStatusEnum> valueMap = new HashMap<>();

    static {
        for (SystemButtonMenuStatusEnum e : SystemButtonMenuStatusEnum.values()) {
            valueMap.put(e.value, e);
        }
    }

    SystemButtonMenuStatusEnum(Integer value, String display) {
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

    public static SystemButtonMenuStatusEnum getByValue(Integer value) {
        return valueMap.get(value);
    }
}
