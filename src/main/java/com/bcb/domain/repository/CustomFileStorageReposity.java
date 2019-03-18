package com.bcb.domain.repository;

import com.bcb.domain.entity.CustomFileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

/**
 * Created by kx on 2018/1/13.
 */
public interface CustomFileStorageReposity extends CrudRepository<CustomFileStorage, Integer>,JpaRepository<CustomFileStorage, Integer> {
    public List<CustomFileStorage> findByIdIn(Collection<Integer> ids);
}
