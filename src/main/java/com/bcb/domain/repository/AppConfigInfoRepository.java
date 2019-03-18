package com.bcb.domain.repository;

import com.bcb.domain.entity.AppConfigInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface AppConfigInfoRepository extends CrudRepository<AppConfigInfo, Integer>,JpaRepository<AppConfigInfo, Integer> {
}
