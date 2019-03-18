package com.bcb.domain.enums;

import java.util.HashMap;
import java.util.Map;


public enum FinancingBaseStatusEnum implements IBaseEnum{
    INITIAL(0, "待开启"),
    COLLECTING(1, "募集中"),
    SUSPENDED(2, "募集暂停"),
    FINISH(3, "募集结束");



    private Integer value;
    private String display;
    private static Map<Integer, FinancingBaseStatusEnum> valueMap = new HashMap<>();

    static {
        for (FinancingBaseStatusEnum e : FinancingBaseStatusEnum.values()) {
            valueMap.put(e.value, e);
        }
    }

    FinancingBaseStatusEnum(Integer value, String display) {
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

    public static FinancingBaseStatusEnum getByValue(Integer value) {
        return valueMap.get(value);
    }
}
