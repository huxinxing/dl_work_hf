package com.bcb.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
@Entity
@Table(name = "system_role_permission")
public class SystemRolePermission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id",unique = true,nullable = false)
    private Integer id;
    @Column(name = "roleId")
    private Integer roleId;
    @Column(name = "menuId")
    private  Integer menuId;
    @Column(name = "crud")
    private String crud;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public String getCrud() {
        return crud;
    }

    public void setCrud(String crud) {
        this.crud = crud;
    }
}
