package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ou.journal.pojo.User;
import com.ou.journal.pojo.UserRole;
import java.util.Optional;


public interface UserRoleRepositoryJPA extends JpaRepository<UserRole, Long> {

    @Query("SELECT ur FROM UserRole ur WHERE ur.user = ?1 AND ur.role.roleName = ?2 ")
    Optional<UserRole> findByUserAndRoleName(User user, String roleName);

    @Query("SELECT ur FROM UserRole ur WHERE ur.user.account.userName = ?1 AND ur.role.roleName = ?2")
    Optional<UserRole> findByUserNameAndRoleName(String userName, String roleName);
}
