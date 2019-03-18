package com.bcb.domain.repository;

import com.bcb.domain.entity.Coin;
import com.bcb.domain.entity.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Integer>, JpaSpecificationExecutor {

    SystemConfiguration findByName(String name);



}
