package com.bcb.domain.repository;

import com.bcb.domain.entity.SystemLoginAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface SystemLoginAccountRepository extends JpaRepository<SystemLoginAccount, Integer> {
    SystemLoginAccount findByLoginName(String loginName);
    List<SystemLoginAccount> findByRole(Integer role);
    List<SystemLoginAccount> findByIdNotAndLoginName(Integer id, String loginName);
}
