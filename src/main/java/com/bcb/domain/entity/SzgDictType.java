package com.bcb.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by black on 18-5-28.
 */
@Data
@Entity
@Table(name = "szg_dict_type")
public class SzgDictType implements Serializable {
    private static final long serialVersionUID = 7987934809916742416L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id; //主键

    @Column(name = "DicTypeCode")
    private String dicTypeCode;  //类别编码

    @Column(name = "DicTypeName")
    private String dicTypeName; //类别名称

    @Column(name = "IsUse")
    private Integer isUse; //是否启用 1启用 0未启用

    @Column(name = "CreateTime")
    private Date createTime; //创建时间
}
