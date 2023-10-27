package com.ou.journal.service.interfaces;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ou.journal.pojo.User;
import com.ou.journal.pojo.UserRole;

public interface UserRoleService {
    UserRole findByUserAndRoleName(User user, String roleName) throws UsernameNotFoundException;
    Optional<UserRole> getByUserAndRoleName(User user, String roleName);
}
