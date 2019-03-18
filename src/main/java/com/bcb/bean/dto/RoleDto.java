package com.bcb.bean.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RoleDto implements Serializable {

    private Integer Id;

    private String RoleName;

    private Date CreateTime;

    private Date ModifyTime;

//    private Boolean hasChild;

    private List<RoleDto> childTree;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer Id) {
        this.Id = Id;
    }

    public String getRoleName() {
		return RoleName;
	}

	public void setRoleName(String roleName) {
		RoleName = roleName;
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

    public List<RoleDto> getChildTree() {
        return childTree;
    }

    public void setChildTree(List<RoleDto> childTree) {
        this.childTree = childTree;
    }

//    public Boolean isHasChild() {
//        return hasChild;
//    }
//
//    public void setHasChild(Boolean hasChild) {
//        this.hasChild = hasChild;
//    }
//
//    public DepartmentDto(Integer id, String departmentName, Integer parentId, Date createTime, Date modifyTime, Boolean hasChild) {
//        Id = id;
//        DepartmentName = departmentName;
//        ParentId = parentId;
//        CreateTime = createTime;
//        ModifyTime = modifyTime;
//        this.hasChild = hasChild;
//    }
//
//    public DepartmentDto(Integer id, String departmentName, Integer parentId, Date createTime, Date modifyTime, Boolean hasChild, DepartmentDto childTree) {
//        Id = id;
//        DepartmentName = departmentName;
//        ParentId = childTree.getId();
//        CreateTime = createTime;
//        ModifyTime = modifyTime;
//        this.hasChild = hasChild;
//    }
//
//    public DepartmentDto() {
//
//    }
}
