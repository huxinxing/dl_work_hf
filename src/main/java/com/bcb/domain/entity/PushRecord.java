package com.bcb.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@Table(name = "push_record")
public class PushRecord implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id",unique = true,nullable = false)
    private Integer id;

    @Column(name = "target_id")
    private String targetId;

    private String title;

    private String message;

    @Column(name = "extra_type")
    private Integer extraType;

    @Column(name = "extra_value")
    private String extraValue;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "user_id")
    private Integer userId;

    private Boolean state;

}
