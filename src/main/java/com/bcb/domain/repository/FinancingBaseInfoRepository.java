package com.bcb.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.bcb.domain.entity.FinancingBaseInfo;

import java.util.List;

public interface FinancingBaseInfoRepository extends JpaRepository<FinancingBaseInfo, String>, JpaSpecificationExecutor {

    FinancingBaseInfo findOneByFinancingUuid(String uuid);



    List<FinancingBaseInfo> findByStatusBetween(int beginStatus, int endStatus);

    FinancingBaseInfo findBySerialNum(String serialNum);

}
