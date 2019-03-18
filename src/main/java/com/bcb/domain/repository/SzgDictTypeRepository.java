package com.bcb.domain.repository;

import com.bcb.domain.entity.SzgDictType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by black on 18-5-28.
 */
public interface SzgDictTypeRepository extends CrudRepository<SzgDictType, Integer>, JpaRepository<SzgDictType, Integer> {

    List<SzgDictType> findByIsUse(Boolean isUse);

    SzgDictType findByDicTypeCodeAndIsUse(String dictTypeCode, Boolean isUse);
}
