package com.bcb.domain.repository;


import com.bcb.domain.entity.PushUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PushUserRepository extends JpaRepository<PushUser, Integer>, JpaSpecificationExecutor {
    PushUser findByUserId(Integer userId);
}
