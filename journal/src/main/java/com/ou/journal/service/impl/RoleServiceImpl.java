package com.ou.journal.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ou.journal.pojo.Role;
import com.ou.journal.repository.RoleRepositoryJPA;
import com.ou.journal.service.interfaces.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepositoryJPA roleRepositoryJPA;

    @Override
    public Role retrieve(String roleName) throws Exception {
        Optional<Role> roleOptional = roleRepositoryJPA.findByRoleName(roleName);
        if (roleOptional.isPresent()) {
            return roleOptional.get();
        } else {
            throw new Exception(String.format("Quyền người dùng %s không tồn tại!", roleName));
        }
    }
    
}
