package com.bcb.domain.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="system_config_info")
public class SystemConfigInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique = true, nullable = false)
    private Integer id;

    @Column(name="google_secret")
    private String googleSecret;

    @Column(name="project_id")
    private Integer projectId;

    @Column(name="sms_channel")
    private String smsChannel;

    @Column(name="is_show_superior_rate_set")
    private Integer isShowSuperiorRateSet;

}
