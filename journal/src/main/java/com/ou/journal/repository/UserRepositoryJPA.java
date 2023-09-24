package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.User;

public interface UserRepositoryJPA extends JpaRepository<User, Long> {
}
