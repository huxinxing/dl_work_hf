package com.bcb.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_financing_settlement")
public class UserFinancingSettlement {

    @Id
    private String id;

    @Column(name = "user_financing_record_id")
    private Integer userFinancingRecordId;

    @Column(name = "principal_bcb_amount")
    private BigDecimal principalBcbAmount;

    @Column(name = "principal_usdx_amount")
    private BigDecimal principalUsdxAmount;

    @Column(name = "principal_rate")
    private BigDecimal principalRate;

    @Column(name = "fixed_rate")
    private BigDecimal fixedRate;

    @Column(name = "fixed_bcb_amount")
    private BigDecimal fixedBcbAmount;

    @Column(name = "fixed_usdx_amount")
    private BigDecimal fixedUsdxAmount;

    private BigDecimal bcb2usdx;

    @Column(name = "extra_bcb_amount")
    private BigDecimal extraBcbAmount;

    @Column(name = "extra_usdx_amount")
    private BigDecimal extraUsdxAmount;

    @Column(name = "settlement_date")
    private Date settlementDate;

    private String remark;

    @Column(name = "foundation_commission")
    private BigDecimal foundationCommission;

    @Column(name = "superior_commission")
    private BigDecimal superiorCommission;

    @Column(name = "superior_commission_tax")
    private BigDecimal superiorCommissionTax;

    @Column(name = "service_rate")
    private BigDecimal serviceRate;

    @Column(name = "service_amount")
    private BigDecimal serviceAmount;

    @Column(name = "modify_time")
    private Timestamp modifyTime;
}
