package com.bcb.domain.repository;

import com.bcb.domain.entity.UserBalanceWithdraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface UserBalanceWithdrawRepository extends CrudRepository<UserBalanceWithdraw, Integer>,JpaRepository<UserBalanceWithdraw, Integer> {

    UserBalanceWithdraw findOneById(Integer id);

}
