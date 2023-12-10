package com.ou.journal.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ou.journal.pojo.Role;
import com.ou.journal.pojo.User;
import com.ou.journal.pojo.UserRole;
import com.ou.journal.repository.UserRoleRepositoryJPA;
import com.ou.journal.service.interfaces.RoleService;
import com.ou.journal.service.interfaces.UserRoleService;

@Service
public class UserRoleServiceImpl implements UserRoleService{

    @Autowired
    private UserRoleRepositoryJPA userRoleRepositoryJPA;

    @Autowired
    private RoleService roleService;

    @Override
    public UserRole findByUserAndRoleName(User user, String roleName) throws UsernameNotFoundException {
        Optional<UserRole> userRole = userRoleRepositoryJPA.findByUserAndRoleName(user, roleName);
        if(userRole.isPresent()){
            return userRole.get();
        } else {
            throw new UsernameNotFoundException("User don't have specific role");
        }
    }

    @Override
    public Optional<UserRole> getByUserAndRoleName(User user, String roleName) {
        return userRoleRepositoryJPA.findByUserAndRoleName(user, roleName);
    }

    @Override
    public void addUserRole(User user, String roleName) throws Exception {
        Role role = roleService.retrieve(roleName);
        UserRole userRole = new UserRole();
        userRole.setRole(role);
        userRole.setUser(user);
        userRoleRepositoryJPA.save(userRole);
    }

    @Override
    public Optional<UserRole> getByUserNameAndRoleName(String userName, String roleName) {
        return userRoleRepositoryJPA.findByUserNameAndRoleName(userName, roleName);
    }
    
    
}
