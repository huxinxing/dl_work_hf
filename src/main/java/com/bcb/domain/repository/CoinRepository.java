package com.bcb.domain.repository;

import com.bcb.domain.entity.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface CoinRepository extends JpaRepository<Coin, Integer>, JpaSpecificationExecutor {
    Coin findByName(String name);
}
