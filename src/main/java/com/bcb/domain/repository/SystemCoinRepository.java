package com.bcb.domain.repository;

import com.bcb.domain.entity.SystemCoin;
import com.bcb.domain.entity.UserBalanceWithdraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;


public interface SystemCoinRepository extends CrudRepository<SystemCoin,Integer>,JpaRepository<SystemCoin,Integer>, JpaSpecificationExecutor {

    SystemCoin findByName(String coinName);


    SystemCoin findOneById(Integer coinId);
}
