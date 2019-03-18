package com.bcb.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
/**
 * Created by jintaoxu
 */
@Entity
@Table(name = "system_login_menu")
public class SystemLoginMenu implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id",unique = true,nullable = false)
	private Integer id;
	@Column(name = "MenuName")
	private String menuName;
	@Column(name = "MenuPath")
	private String menuPath;
	@Column(name = "MenuKey")
	private String menuKey;
	@Column(name = "Sort")
	private Integer sort;
	@Column(name = "Status")
	private Integer status;
	@Column(name = "ParentId")
	private Integer parentId;
	@Column(name = "CreateTime")
	private Date createTime;
	@Column(name = "Button")
	private Integer button;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuPath() {
		return menuPath;
	}

	public void setMenuPath(String menuPath) {
		this.menuPath = menuPath;
	}

	public String getMenuKey() {
		return menuKey;
	}

	public void setMenuKey(String menuKey) {
		this.menuKey = menuKey;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getButton() {
		return button;
	}

	public void setButton(Integer button) {
		this.button = button;
	}
}
