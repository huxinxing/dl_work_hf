package com.bcb.domain.repository;

import com.bcb.domain.entity.SystemBusinessDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by kx on 2018/2/3.
 */
public interface SystemBusinessDictionaryRepository extends CrudRepository<SystemBusinessDictionary, String>,JpaRepository<SystemBusinessDictionary, String> {
    public List<SystemBusinessDictionary> findByGroupId(Integer groupId);
}
