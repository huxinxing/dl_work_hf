package com.bcb.domain.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 投资交易状态
 * Created by liq on 2018/1/28.
 */
public enum TradeStatusEnum implements IBaseEnum {
    CONFIRM(0, "投资已确认"),//收到用户投资但是没有返BCB
    SUCCEED(1, "投资已成功"),//已经返回BCB
    FAILURE(-1, "投资失败");

    private Integer value;
    private String display;
    private static Map<Integer, TradeStatusEnum> valueMap = new HashMap<>();

    static {
        for (TradeStatusEnum e : TradeStatusEnum.values()) {
            valueMap.put(e.value, e);
        }
    }

    TradeStatusEnum(Integer value, String display) {
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

    public static TradeStatusEnum getByValue(Integer value) {
        return valueMap.get(value);
    }
}
