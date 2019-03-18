package com.bcb.bean.dto;

import java.io.Serializable;

public class UserInfoparent implements Serializable {
    private static final long serialVersionUID = -2207344252173230075L;
    private Integer userId;
    private Integer displayAgentLevel; // 显示代理等级层次

    private Integer parentId;

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getDisplayAgentLevel() {
        return displayAgentLevel;
    }

    public void setDisplayAgentLevel(Integer displayAgentLevel) {
        this.displayAgentLevel = displayAgentLevel;
    }
}
