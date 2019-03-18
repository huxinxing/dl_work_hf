package com.bcb.domain.repository;

import com.bcb.domain.entity.UserGoldexperienceRelation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public interface UserGoldexperienceRelationRepository extends CrudRepository<UserGoldexperienceRelation,Integer>,JpaRepository<UserGoldexperienceRelation,Integer>, JpaSpecificationExecutor {

    List<UserGoldexperienceRelation> findByGeId(Integer geId);

    UserGoldexperienceRelation findFirstByExperienceStatusOrderByReceiveDateDesc(Integer experienceStatus);

    UserGoldexperienceRelation findFirstByExperienceStatusAndExperienceEndTimeBefore(int i, java.sql.Date date);

    List<UserGoldexperienceRelation> findByExperienceStatusAndExperienceEndTimeBetween(Integer experienceStatus, Timestamp beginTime,Timestamp endTime);
}
