package com.bcb.domain.repository;

import com.bcb.domain.entity.FinancingEnInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface FinancingEnInfoRepository extends CrudRepository<FinancingEnInfo, String>,JpaRepository<FinancingEnInfo, String> {
    FinancingEnInfo findOneByFinancingUuid(String uuid);
}
