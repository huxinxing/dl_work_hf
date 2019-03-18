package com.bcb.domain.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liq on 2018/2/3.
 */
public enum WithdrawEnum implements IBaseEnum {
    WAITING(0, "待审核"),
    AUDIT(1, "已审核"),
    FINISH(2, "交易已确认"),
    REFUSED(3, "已拒绝");

    private Integer value;
    private String display;
    private static Map<Integer, WithdrawEnum> valueMap = new HashMap<>();

    static {
        for (WithdrawEnum e : WithdrawEnum.values()) {
            valueMap.put(e.value, e);
        }
    }

    WithdrawEnum(Integer value, String display) {
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

    public static WithdrawEnum getByValue(Integer value) {
        return valueMap.get(value);
    }
}
