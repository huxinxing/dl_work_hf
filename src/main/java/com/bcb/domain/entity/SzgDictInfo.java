package com.bcb.domain.entity;

/**
 * Created by black on 18-5-26.
 */

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 数据字典表
 */
@Data
@Entity
@Table(name = "szg_dict_info")
public class SzgDictInfo implements Serializable {

    private static final long serialVersionUID = -1588127363741752768L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DicId")
    private Integer dicId; //主键

    @Column(name = "DicTypeId")
    private Integer dicTypeId;  //类别Id

    @Column(name = "DicKey")
    private String dicKey; //配置项名称

    @Column(name = "DicValue")
    private String dicValue;  //配置项值

    @Column(name = "DicLangFlag")
    private String dicLangFlag; //配置项语言标志

    @Column(name = "IsUse")
    private Boolean isUse; //是否启用 1启用 0未启用

    @Column(name = "SortIndex")
    private Integer sortIndex; //排序子段

    @Column(name = "CreateTime")
    private Date createTime; //创建时间

}
