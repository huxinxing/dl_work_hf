package com.bcb.domain.repository;

import com.bcb.domain.entity.FinancingActivityGoldexperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface FinancingActivityGoldexperienceRepository extends CrudRepository<FinancingActivityGoldexperience,Integer>,JpaRepository<FinancingActivityGoldexperience,Integer>, JpaSpecificationExecutor {

    FinancingActivityGoldexperience findByGeId(Integer geId);

}
