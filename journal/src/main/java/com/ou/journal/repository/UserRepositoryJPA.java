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
    @Query("SELECT u FROM User u JOIN UserRole ur ON u.id = ur.user.id WHERE ur.role.roleName = ?1")
    List<User> findByRoleName(String roleName);
}
