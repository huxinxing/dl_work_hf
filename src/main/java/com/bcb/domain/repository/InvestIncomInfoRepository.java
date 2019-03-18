package com.bcb.domain.repository;

import com.bcb.domain.entity.InvestIncomInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by kx on 2018/2/8.
 */
public interface InvestIncomInfoRepository extends CrudRepository<InvestIncomInfo, Integer>,JpaRepository<InvestIncomInfo, Integer> {
    public InvestIncomInfo findByUserFinancingRelationId(Integer financingId);
}
