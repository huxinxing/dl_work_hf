package com.bcb.domain.enums;

import java.util.HashMap;
import java.util.Map;

public enum FinancingWithdrawEnum implements IBaseEnum {
    WAITING(0, "待审核"),
    AUDIT(1, "已审核"),
    FINISH(2, "交易已确认"),
    REFUSED(3, "已拒绝");

    private Integer value;
    private String display;
    private static Map<Integer, FinancingWithdrawEnum> valueMap = new HashMap<>();

    static {
        for (FinancingWithdrawEnum e : FinancingWithdrawEnum.values()) {
            valueMap.put(e.value, e);
        }
    }

    FinancingWithdrawEnum(Integer value, String display) {
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

    public static FinancingWithdrawEnum getByValue(Integer value) {
        return valueMap.get(value);
    }
}
