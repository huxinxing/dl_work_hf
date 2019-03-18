package com.bcb.domain.repository;

import com.bcb.domain.entity.FinancingDailyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public interface FinancingDailyRateRepository extends JpaRepository<FinancingDailyRate, Integer>, JpaSpecificationExecutor{
    FinancingDailyRate findByCreateTime(Timestamp date);

    List<FinancingDailyRate> findByOrderByCreateTimeDesc();

    FinancingDailyRate findFirstByOrderByCreateTimeDesc();
}
