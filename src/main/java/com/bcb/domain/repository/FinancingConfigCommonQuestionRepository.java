package com.bcb.domain.repository;

import com.bcb.domain.entity.FinancingConfigCommonQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FinancingConfigCommonQuestionRepository extends CrudRepository<FinancingConfigCommonQuestion,Integer>,JpaRepository<FinancingConfigCommonQuestion,Integer>, JpaSpecificationExecutor {

    FinancingConfigCommonQuestion findFirstByCcqLangOrderByCcqLangAscriptionDesc(String lang);

    FinancingConfigCommonQuestion findByCcqId(Integer ccqId);

    List<FinancingConfigCommonQuestion> findByCcqLangAscription(Integer ccqLangAscription);

    FinancingConfigCommonQuestion findByCcqLangAndCcqLangAscription(String lang,Integer ccqLangAscription);

}
