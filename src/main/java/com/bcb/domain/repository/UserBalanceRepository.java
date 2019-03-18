package com.bcb.domain.repository;


import com.bcb.domain.entity.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserBalanceRepository extends JpaRepository<UserBalance, Integer>, JpaSpecificationExecutor {
    UserBalance findByUserIdAndCoinId(Integer userId, Integer coinId);
}
