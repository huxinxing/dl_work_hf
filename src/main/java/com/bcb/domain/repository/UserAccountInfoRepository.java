package com.bcb.domain.repository;

import com.bcb.domain.entity.UserAccountInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by kx on 2018/1/16.
 */
public interface UserAccountInfoRepository extends CrudRepository<UserAccountInfo, Integer>,JpaRepository<UserAccountInfo, Integer> {

    public List<UserAccountInfo> findByParentId(Integer pid);

    public UserAccountInfo findByUserId(Integer userId);

}
