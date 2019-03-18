package com.bcb.bean;

/**
 * Created by kx on 2018/1/12.
 */
public enum ResponseStatus {
    USER_ROLE_WRONG("10010","角色正在使用，不能删除"),
    PATH_NOT_FOUND("404","路径错误"),
    CODE_WRONG("500","程序错误"),
    SESSION_INVALID("401","session失效"),
    SUCCESS("200","请求成功"),
    USER_NOT_FUND("1001","用户不存在"),
    USER_SAME("1000","用户已存在"),
    USER_PASS_WRONG("1002","用户密码错误"),
    USER_NOT_SUPPER("1003","用户非超级管理员"),
    DATA_WRONG("1004","数据格式错误"),
    DATA_SAME_IMPORT("1005","已导入过相同项目相同钱包地址"),
    DATA_PROJECT_NOT_FUND("1006","钱包地址描述文件导入失败"),
    DATA_VERIFY_NOT_PASS("1007","签名校验失败"),
    CODE_NOPRESSMISSION("1008","无权限");

    private String key;
    private String value;
    private ResponseStatus(String key, String value){
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }
    public String getValue( )
    {
        return this.value;
    }
}
