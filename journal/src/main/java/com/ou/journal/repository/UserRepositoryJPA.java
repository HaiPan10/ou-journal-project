package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.User;
import java.util.Optional;


public interface UserRepositoryJPA extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
