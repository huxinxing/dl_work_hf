package com.bcb.bean;

/**
 * Created by liq on 2018/1/22.
 */
public class KeyValueBean {

    private String key;

    private String value;

    public KeyValueBean() {
    }

    public KeyValueBean(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
