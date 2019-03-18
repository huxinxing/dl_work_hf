package com.bcb.domain.repository;

import com.bcb.domain.entity.BlockOperationInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by liq on 2018/3/20.
 */
public interface BlockOperationInfoRepository extends JpaRepository<BlockOperationInfo, Integer>, JpaSpecificationExecutor  {

    BlockOperationInfo findByBusinessId(String businessId);

    BlockOperationInfo findFirstByStatue (Integer statue);

}
