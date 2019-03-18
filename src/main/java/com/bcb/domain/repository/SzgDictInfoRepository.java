package com.bcb.domain.repository;

import com.bcb.domain.entity.SzgDictInfo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by black on 18-5-28.
 */
public interface SzgDictInfoRepository extends CrudRepository<SzgDictInfo, Integer>, JpaRepository<SzgDictInfo, Integer> {
	
	List<SzgDictInfo> findByDicTypeId(Integer dicTypeId);

	List<SzgDictInfo> findByDicTypeIdAndDicLangFlag(Integer dicType,String dicLangFlag);

	SzgDictInfo findOneByDicTypeIdAndDicLangFlagAndDicKey(Integer dicType,String dicLangFlag,String dicKey);

	SzgDictInfo findOneByDicTypeIdAndDicValue(Integer dicType,String dicValue);

}
