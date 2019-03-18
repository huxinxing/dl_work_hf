package com.bcb.domain.repository;

import com.bcb.domain.entity.FinancingHomeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FinancingHomeConfigRepository extends CrudRepository<FinancingHomeConfig,Integer>,JpaRepository<FinancingHomeConfig,Integer>, JpaSpecificationExecutor{

    Integer countByType(Integer type);

    FinancingHomeConfig findByTypeAndTypeId(Integer hcId,Integer typeId);

    FinancingHomeConfig findByTypeAndTypeIdAndStatus(Integer hcId,Integer typeId,Integer status);

    List<FinancingHomeConfig> findByTypeAndStatus(Integer type,Integer status);

}