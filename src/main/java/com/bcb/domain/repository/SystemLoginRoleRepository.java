package com.bcb.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.bcb.domain.entity.SystemLoginRole;

import java.util.List;

public interface SystemLoginRoleRepository  extends CrudRepository<SystemLoginRole,Integer>,JpaRepository<SystemLoginRole,Integer> {
    public List<SystemLoginRole> findSystemLoginRoleByRoleName(String roleName);
}
