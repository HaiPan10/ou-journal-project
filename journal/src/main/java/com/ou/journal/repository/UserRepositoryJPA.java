package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ou.journal.pojo.User;

import java.util.List;
import java.util.Optional;


public interface UserRepositoryJPA extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("SELECT u.id, u.email, u.lastName, u.firstName FROM User u ")
    List<Object[]> listUser();
}
