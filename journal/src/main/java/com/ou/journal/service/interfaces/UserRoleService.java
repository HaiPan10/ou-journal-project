package com.ou.journal.service.interfaces;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ou.journal.pojo.User;
import com.ou.journal.pojo.UserRole;

public interface UserRoleService {
    UserRole findByUserAndRoleName(User user, String roleName) throws UsernameNotFoundException;
}
