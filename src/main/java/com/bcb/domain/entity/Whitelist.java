package com.bcb.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "system_except_whitelist")
public class Whitelist implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",unique = true,nullable = false)
    private Integer id;


    private String address;

    @Column(name = "modify_time")
    private Timestamp modifyTime;

    private String remark;

    private Boolean state;
}
