package com.bcb.domain.repository;

import com.bcb.domain.entity.SystemConfigInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SystemConfigInfoRepository extends CrudRepository<SystemConfigInfo, Integer>,JpaRepository<SystemConfigInfo, Integer> {
      SystemConfigInfo findByProjectId(Integer  projectId);

}
