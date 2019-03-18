package com.bcb.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.bcb.domain.entity.UserAgentsFinancingBill;

/**
 * @author
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date 4/24/201810:01 PM
 */
public interface UserAgentsFinancingBillRepository extends CrudRepository<UserAgentsFinancingBill, Integer>,JpaRepository<UserAgentsFinancingBill, Integer>  {
    List<UserAgentsFinancingBill> findByRecordIdAndUserId(Integer recordId, Integer userId);
}
