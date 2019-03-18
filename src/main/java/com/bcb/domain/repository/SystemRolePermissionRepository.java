package com.bcb.domain.repository;

import com.bcb.domain.entity.SystemRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface SystemRolePermissionRepository extends CrudRepository<SystemRolePermission, Integer>,JpaRepository<SystemRolePermission, Integer> {

    SystemRolePermission findFirstByMenuIdAndRoleId(Integer menuId,Integer roleId);

}
