package com.bcb.bean.dto;

import com.bcb.domain.entity.SystemLoginMenu;
import com.bcb.domain.entity.SystemLoginRole;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

public class SystemLoginRoleAndMenu {
    private Integer id;
    private String roleName;
    private Date createTime;
    private Date modifyTime;
    private   List<SystemLoginMenu>  listSystemLoginMenu;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public List<SystemLoginMenu> getListSystemLoginMenu() {
        return listSystemLoginMenu;
    }

    public void setListSystemLoginMenu(List<SystemLoginMenu> listSystemLoginMenu) {
        this.listSystemLoginMenu = listSystemLoginMenu;
    }
}
