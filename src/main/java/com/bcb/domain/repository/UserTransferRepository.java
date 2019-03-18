package com.bcb.domain.repository;


import com.bcb.domain.entity.UserTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

public interface UserTransferRepository extends JpaRepository<UserTransfer, Integer>, JpaSpecificationExecutor {

    List<UserTransfer> findByTypeAndModifyTimeBetween(Integer Type, Date beginTime,Date endTime);

}
