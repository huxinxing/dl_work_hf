package com.bcb.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "system_login_department")
public class SystemLoginDepartment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id",unique = true,nullable = false)
    private Integer id;

    @Column(name = "DepartmentName")
    private String DepartmentName;

    @Column(name = "ParentId")
    private Integer ParentId;

    @Column(name = "CreateTime")
    private Date CreateTime;

    @Column(name = "ModifyTime")
    private Date ModifyTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String departmentName) {
        DepartmentName = departmentName;
    }

    public Integer getParentId() {
        return ParentId;
    }

    public void setParentId(Integer parentId) {
        ParentId = parentId;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
    }

    public Date getModifyTime() {
        return ModifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        ModifyTime = modifyTime;
    }
}
