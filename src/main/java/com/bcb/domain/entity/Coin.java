package com.bcb.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "system_coin")
public class Coin {

    @Id
    private Integer id;

    private String name;

    private String description;

    private String regex;

    @Column(name = "modify_time")
    private Timestamp modifyTime;

    @Column(name = "code_type")
    private Integer codeType;

    /**
     * 精度
     */
    private Integer precisions;
}
