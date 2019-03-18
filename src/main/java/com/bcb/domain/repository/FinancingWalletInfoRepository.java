package com.bcb.domain.repository;

import com.bcb.domain.entity.FinancingWalletInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface FinancingWalletInfoRepository extends CrudRepository<FinancingWalletInfo, Integer>,JpaRepository<FinancingWalletInfo, Integer> {
    List<FinancingWalletInfo> findByFinancingUuid(String financing);
}
