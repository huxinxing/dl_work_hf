package com.bcb.bean;

/**
 * Created by kx on 2018/1/12.
 */
public class ResponseResult {
    private String status;

    private String code;

    private String message;

    private Object data;

    public ResponseResult(String status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
    public ResponseResult(String status, String code, Object data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
