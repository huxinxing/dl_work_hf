package com.bcb.domain.repository;

import com.bcb.domain.entity.FinancingIncomeUnknown;
import com.bcb.domain.entity.FinancingWalletInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;


public interface FinancingIncomeUnknownRepository extends CrudRepository<FinancingIncomeUnknown, Integer>,JpaRepository<FinancingIncomeUnknown, Integer> {
}
