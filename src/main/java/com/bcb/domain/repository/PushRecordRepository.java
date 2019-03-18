package com.bcb.domain.repository;

import com.bcb.domain.entity.PushRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PushRecordRepository extends JpaRepository<PushRecord, Integer>, JpaSpecificationExecutor {

}
