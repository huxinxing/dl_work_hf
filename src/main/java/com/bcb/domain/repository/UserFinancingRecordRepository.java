package com.bcb.domain.repository;


import com.bcb.domain.entity.UserFinancingRecord;

import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserFinancingRecordRepository extends JpaRepository<UserFinancingRecord, Integer>, JpaSpecificationExecutor {
    List<UserFinancingRecord> findAllByRecordStatus(Integer status, Pageable pageable);

    //List<UserFinancingRecord> findAllByRecordStatusAndAgentsStatus(Integer status, Integer agentsStatus, Pageable pageable);

    UserFinancingRecord findFirstByRecordStatusAndAgentsStatus(Integer status, Integer agentsStatus);

    UserFinancingRecord findFirstByRecordStatusAndExpireTimeBeforeOrderByExpireTimeDesc(Integer status, Date date);

    List<UserFinancingRecord> findByFinancingUuid(String financingUuid);

    List<UserFinancingRecord> findByUserId(Integer userId);

    List<UserFinancingRecord> findByBlockCreateTimeBetween(Date beginTime, Date endTime);

    List<UserFinancingRecord> findByRecordCreateTimeBetween(Date beginTime, Date endTime);

    UserFinancingRecord findOneById(Integer recordId);

    List<UserFinancingRecord> findByRecordCreateTimeBetweenAndRecordStatusBetween(Date beginTime,Date endTime,Integer startStatus,Integer endStatus);

    Integer countByTxId(String txId);

    UserFinancingRecord findFirstByRecordStatus(Integer recordStatus);

    List<UserFinancingRecord> findByRecordStatusAndExpireTimeBetween(Integer recordStrtus,Timestamp start, Timestamp end);
}
