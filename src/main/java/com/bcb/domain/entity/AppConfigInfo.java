package com.bcb.domain.entity;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "app_config_info")
public class AppConfigInfo implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id",unique = true,nullable = false)
    private Integer id;

    @Column(name = "Volatility")
    private String volatility;

    private String langs;

    public String getLangs() {
        return langs;
    }

    public void setLangs(String langs) {
        this.langs = langs;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVolatility() {
        return volatility;
    }

    public void setVolatility(String volatility) {
        this.volatility = volatility;
    }

    @Column(name="IsAllowWithdraw")
    private  Integer isAllowWithdraw;

    public Integer getIsAllowWithdraw() {
        return isAllowWithdraw;
    }

    public void setIsAllowWithdraw(Integer isAllowWithdraw) {
        this.isAllowWithdraw = isAllowWithdraw;
    }
    @Column(name="IsCertification")
    private  Integer isCertification;

    public Integer getIsCertification() {
        return isCertification;
    }

    public void setIsCertification(Integer isCertification) {
        this.isCertification = isCertification;
    }


}
