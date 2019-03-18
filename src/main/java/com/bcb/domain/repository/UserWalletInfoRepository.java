package com.bcb.domain.repository;

import com.bcb.domain.entity.UserWalletInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by kx on 2018/1/16.
 */
public interface UserWalletInfoRepository extends CrudRepository<UserWalletInfo, Integer>,JpaRepository<UserWalletInfo, Integer> {
    List<UserWalletInfo> findByUserId(Integer userId);

    List<UserWalletInfo> findByUserIdAndStatusNot(Integer userId,Integer status);

    UserWalletInfo findFirstByTokenAndStatusNot(String token,Integer status);

    List<UserWalletInfo> findByStatusBetweenAndUserId(Integer beginStatus,Integer endStatus,Integer userId);

}
