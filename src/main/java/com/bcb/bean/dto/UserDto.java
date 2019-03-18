package com.bcb.bean.dto;

import java.io.Serializable;

/**
 * Created by kx on 2018/1/12.
 */
public class UserDto implements Serializable{

    private String userId;

    private String userName;

    private String ethToken;

    public UserDto(String userId,String userName,String ethToken) {
        this.userId = userId;
        this.userName = userName;
        this.ethToken = ethToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEthToken() {
        return ethToken;
    }

    public void setEthToken(String ethToken) {
        this.ethToken = ethToken;
    }
}
