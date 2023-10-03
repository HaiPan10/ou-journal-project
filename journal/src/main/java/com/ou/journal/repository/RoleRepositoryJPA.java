package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.Role;
import java.util.Optional;


public interface RoleRepositoryJPA extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);
}
