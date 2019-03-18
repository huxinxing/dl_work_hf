package com.bcb.domain.repository;

import com.bcb.domain.entity.FinancingActivityInterstCoupons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface FinancingActivityInterstCouponsRepository  extends CrudRepository<FinancingActivityInterstCoupons,Integer>,JpaRepository<FinancingActivityInterstCoupons,Integer>, JpaSpecificationExecutor {
    FinancingActivityInterstCoupons findByIcId(Integer icId);
}
