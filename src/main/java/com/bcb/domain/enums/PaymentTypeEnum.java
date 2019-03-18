package com.bcb.domain.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 投资类型
 * @create by wenqiang 2018-04-03
 */
public enum PaymentTypeEnum implements IBaseEnum {

    USDX(1, "USDX"),
    ETH(2, "ETH");

    private Integer value;
    private String display;
    private static Map<Integer, PaymentTypeEnum> valueMap = new HashMap<>();

    static {
        for (PaymentTypeEnum e : PaymentTypeEnum.values()) {
            valueMap.put(e.value, e);
        }
    }

    PaymentTypeEnum(Integer value, String display) {
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

    public static PaymentTypeEnum getByValue(Integer value) {
        return valueMap.get(value);
    }
}
