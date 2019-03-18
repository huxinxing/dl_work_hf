package com.bcb.domain.entity;

import lombok.Data;
import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "System_coin")
public class SystemCoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "id")
    private int id;

    @Column( name = "name")
    private String name;

    @Column( name = "description")
    private String description;

    @Column( name = "regex")
    private String regex;

    @Column( name = "modify_time")
    private Timestamp modify_time;

    @Column( name = "code_type")
    private int codeType;

    @Column( name = "precisions")
    private int precisions;

}
