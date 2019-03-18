package com.bcb.domain.repository;

import com.bcb.domain.entity.UserInterstCouponsRelation;
import org.omg.CORBA.INTERNAL;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public interface UserInterstCouponsRelationRepository  extends CrudRepository<UserInterstCouponsRelation,Integer>,JpaRepository<UserInterstCouponsRelation,Integer>, JpaSpecificationExecutor {

    List<UserInterstCouponsRelation> findByIcId(Integer icId);

    UserInterstCouponsRelation findFirstByIcStatusOrderByReceiveDateDesc(Integer icStatuc);

    UserInterstCouponsRelation findFirstByIcStatusAndIcEndTimeBefore(int i, java.sql.Date date);

    UserInterstCouponsRelation findByRecordId(Integer recordId);

    List<UserInterstCouponsRelation> findByBcb2UsdxIsNotNullAndIcStatusAndIcEndTimeBetween(Integer icStatus, Timestamp beginTime,Timestamp endTime);
}
