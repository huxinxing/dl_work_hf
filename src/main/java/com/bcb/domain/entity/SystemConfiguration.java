package com.bcb.domain.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "system_configuration")
public class SystemConfiguration {

    public final static String SERVICE_RATE = "ServiceRate";
    public final static String TAX_RATE = "TaxRate";

    @Id
    private Integer id;

    private String name;

    private String description;

    private String value;

    @Column(name = "modify_time")
    private Timestamp modifyTime;
}
