package com.bcb.domain.repository;

import com.bcb.domain.entity.SystemLogSms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SystemLogSmsRepository extends JpaRepository<SystemLogSms, Integer>,JpaSpecificationExecutor {
}
