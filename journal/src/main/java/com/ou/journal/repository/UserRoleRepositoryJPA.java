package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.UserRole;

public interface UserRoleRepositoryJPA extends JpaRepository<UserRole, Long> {
    
}
