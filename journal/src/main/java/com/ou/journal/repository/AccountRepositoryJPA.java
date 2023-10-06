package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ou.journal.pojo.Account;

import java.util.Optional;

public interface AccountRepositoryJPA extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByUserName(String userName);
    Optional<Account> findById(Long id);
}
