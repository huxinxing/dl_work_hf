package com.bcb.bean.dto;

import javax.persistence.Column;
import java.io.Serializable;

public class SystemRoleAddDto implements Serializable {
    private Integer id;
    private String roleName;
    private Integer[] menuIdArray;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer[] getMenuIdArray() {
        return menuIdArray;
    }

    public void setMenuIdArray(Integer[] menuIdArray) {
        this.menuIdArray = menuIdArray;
    }
}
