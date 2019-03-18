package com.bcb.bean.dto;

import java.io.Serializable;
import java.util.List;

public class UserInfoResultDto   implements Serializable {
    private static final long serialVersionUID = -2207344252173230075L;

    private UserInfoperson userInfoperson;
    private List<UserInfoparent> userInfoparentList;
    private List<UserInfochild> userInfochildList;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public UserInfoperson getUserInfoperson() {
        return userInfoperson;
    }

    public void setUserInfoperson(UserInfoperson userInfoperson) {
        this.userInfoperson = userInfoperson;
    }

    public List<UserInfoparent> getUserInfoparentList() {
        return userInfoparentList;
    }

    public void setUserInfoparentList(List<UserInfoparent> userInfoparentList) {
        this.userInfoparentList = userInfoparentList;
    }

    public List<UserInfochild> getUserInfochildList() {
        return userInfochildList;
    }

    public void setUserInfochildList(List<UserInfochild> userInfochildList) {
        this.userInfochildList = userInfochildList;
    }
}

