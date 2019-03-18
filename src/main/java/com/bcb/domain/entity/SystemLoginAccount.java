package com.bcb.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by kx on 2018/1/24.
 */
@Data
@Entity
@Table(name = "system_login_account")
public class SystemLoginAccount implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",unique = true,nullable = false)
    private Integer id;

    @Column(name = "login_name")
    private String loginName;

    private String password;

    private String salt;

    private Integer role;

    private Integer status;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "department_id")
    private Integer departmentId;

    @Transient
    private String time;

    @Transient
    private String hasRight="0";

    @Transient
    private String createTimeStr;

    public String getCreateTimeStr() {
        return  this.getCreateTime().toString();
    }

}
