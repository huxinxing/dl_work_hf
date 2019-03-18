package com.bcb.domain.repository;

import com.bcb.domain.entity.SystemLogOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface SystemLogOperationRepository extends JpaRepository<SystemLogOperation, Integer>,JpaSpecificationExecutor {
}
