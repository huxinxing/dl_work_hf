package com.bcb.domain.repository;

import com.bcb.domain.entity.Whitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface WhitelistRepository extends JpaRepository<Whitelist, Integer>, JpaSpecificationExecutor {
    Whitelist findByAddress(String address);

    List<Whitelist> findByState(Boolean state);

    Whitelist findOneById(Integer whiteId);
}
