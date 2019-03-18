package com.bcb.domain.repository;


import com.bcb.domain.entity.UserFinancingSettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

public interface UserFinancingSettlementRepository extends JpaRepository<UserFinancingSettlement, Integer>, JpaSpecificationExecutor {

    List<UserFinancingSettlement> findBySettlementDateBetween(Date beginTime,Date endTime);

    List<UserFinancingSettlement> findByUserFinancingRecordId(Integer recordId);



}
