package com.bcb.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@Entity
@Table(name = "financing_daily_rate")
public class FinancingDailyRate {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "Id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "Bcb2Usdx")
    private BigDecimal bcb2Usdx;

    @Column(name = "Usdx2Cny")
    private BigDecimal usdx2Cny;

    @Column(name = "DailyRate")
    private BigDecimal dailyRate;

    @Column(name = "Bcb2Cny")
    private BigDecimal bcb2Cny;

    @Column(name="Quotation")
    private String quotation;

    @Column(name="CreateTime")
    private Date createTime;

    @Transient
    private String createTimeStr;

    public String getCreateTimeStr() {
        return this.createTime==null?"":this.createTime.toString();
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = this.createTime==null?"":this.createTime.toString();
    }

    public FinancingDailyRate(BigDecimal bcb2Usdx, BigDecimal usdx2Cny, BigDecimal dailyRate, BigDecimal bcb2Cny, String quotation, Date createTime) {
        this.bcb2Usdx = bcb2Usdx;
        this.usdx2Cny = usdx2Cny;
        this.dailyRate = dailyRate;
        this.bcb2Cny = bcb2Cny;
        this.quotation = quotation;
        this.createTime = createTime;
    }
}
