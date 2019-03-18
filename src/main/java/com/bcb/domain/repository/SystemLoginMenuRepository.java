package com.bcb.domain.repository;

import com.bcb.domain.entity.SystemLoginMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface SystemLoginMenuRepository extends CrudRepository<SystemLoginMenu, Integer>,JpaRepository<SystemLoginMenu, Integer> {

    SystemLoginMenu findFirstByMenuKey(String menuKey);

}
