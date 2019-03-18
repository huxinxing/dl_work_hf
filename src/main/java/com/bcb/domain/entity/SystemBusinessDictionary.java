package com.bcb.domain.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by kx on 2018/2/3.
 */
@Entity
@Table(name = "system_business_dictionary")
public class SystemBusinessDictionary implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id",unique = true,nullable = false)
    private Integer id;

    @Column(name = "GroupId")
    private Integer groupId;

    @Column(name = "GroupName")
    private String groupName;

    @Column(name = "Code")
    private String code;

    @Column(name = "DisplayName")
    private String displayName;

    @Column(name = "Rank")
    private String rank;

    @Column(name = "Status")
    private String status;

    @Column(name = "Remark")
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
