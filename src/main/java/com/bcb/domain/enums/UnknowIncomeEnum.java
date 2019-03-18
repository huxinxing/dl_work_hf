package com.bcb.domain.enums;

public enum UnknowIncomeEnum implements IBaseEnum {
    STATUS_INIT(0, "新建未绑定钱包地址投资"),
    STATUS_DEALED(1, "投资已成功");

    private Integer value;
    private String display;

    UnknowIncomeEnum(Integer value, String display) {
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
}
