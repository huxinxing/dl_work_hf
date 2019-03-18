package com.bcb.domain.enums;

import java.util.HashMap;
import java.util.Map;

public enum UserTransferTypeEnum implements IBaseEnum {
    投资收入(1, "投资收入"),
    投资支出(2, "投资支出"),
    充币(3, "充币"),
    提币(4, "提币");


    private Integer value;
    private String display;
    private static Map<Integer, UserTransferTypeEnum> valueMap = new HashMap<>();

    static {
        for (UserTransferTypeEnum e : UserTransferTypeEnum.values()) {
            valueMap.put(e.value, e);
        }
    }

    UserTransferTypeEnum(Integer value, String display) {
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

    public static UserTransferTypeEnum getByValue(Integer value) {
        return valueMap.get(value);
    }
}
