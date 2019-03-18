package com.bcb.domain.repository;

import com.bcb.domain.entity.SystemButtonMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SystemButtonMenuRepository extends CrudRepository<SystemButtonMenu, Integer>,JpaRepository<SystemButtonMenu, Integer> {
    List<SystemButtonMenu> querySystemButtonMenusByMenuIdAndStatus(Integer MenuId,Integer status);
}
