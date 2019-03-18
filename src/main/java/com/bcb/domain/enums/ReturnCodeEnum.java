package com.bcb.domain.enums;

/**
 * 接口返回码
 *
 * @author liqi
 */
public enum ReturnCodeEnum {
    参数错误("100"),
    成功("200"),
    内部错误("500"),
    业务错误("600"),
    网络不通("400");

    private String value;

    private ReturnCodeEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
}
