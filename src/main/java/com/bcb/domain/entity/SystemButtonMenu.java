package com.bcb.domain.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "system_button_menu")
public class SystemButtonMenu implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id",unique = true , nullable = false)
    private Integer id;

    @Column(name = "MenuId")
    private Integer menuId;

    @Column(name = "ButtonName")
    private String buttonName;

    @Column(name = "ButtonKey")
    private String buttonKey;

    @Column(name = "Status")
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getButtonKey() {
        return buttonKey;
    }

    public void setButtonKey(String buttonKey) {
        this.buttonKey = buttonKey;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
