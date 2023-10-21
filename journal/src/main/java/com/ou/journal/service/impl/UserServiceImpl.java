package com.ou.journal.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ou.journal.enums.RoleName;
import com.ou.journal.pojo.Role;
import com.ou.journal.pojo.User;
import com.ou.journal.pojo.UserRole;
import com.ou.journal.repository.UserRepositoryJPA;
import com.ou.journal.repository.UserRoleRepositoryJPA;
import com.ou.journal.service.interfaces.RoleService;
import com.ou.journal.service.interfaces.UserService;

import jakarta.transaction.Transactional;

@Service
@Transactional (rollbackOn = Exception.class)
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepositoryJPA userRepositoryJPA;

    @Autowired
    private UserRoleRepositoryJPA userRoleRepositoryJPA;

    @Autowired
    private RoleService roleService;

    @Override
    public User create(User user, String roleName) throws Exception {
        try {
            user.setCreatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            user.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            userRepositoryJPA.save(user);
            Role role = roleService.retrieve(roleName);
            UserRole userRole = new UserRole(user, role);
            userRoleRepositoryJPA.save(userRole);
            return user;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public User createAuthorUser(User user) throws Exception {
        try {
            return create(user, RoleName.ROLE_AUTHOR.toString());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public User retrieve(Long id) throws Exception {
        Optional<User> userOptional = userRepositoryJPA.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new Exception("Người dùng không hợp lệ!");
        }
    }

    @Override
    public User findByEmail(String email) throws Exception {
        Optional<User> userOptional = userRepositoryJPA.findByEmail(email);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new Exception("Người dùng không hợp lệ!");
        }
    }
    
}
