package com.bcb.domain.repository;

import com.bcb.domain.entity.FinancingCnInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;


public interface FinancingCnInfoRepository extends CrudRepository<FinancingCnInfo, String>,JpaRepository<FinancingCnInfo, String> {
    FinancingCnInfo findOneByFinancingUuid(String uuid);
}
