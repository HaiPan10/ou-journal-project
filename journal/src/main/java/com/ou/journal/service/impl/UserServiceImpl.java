package com.ou.journal.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ou.journal.enums.RoleName;
import com.ou.journal.pojo.Manuscript;
import com.ou.journal.pojo.Role;
import com.ou.journal.pojo.User;
import com.ou.journal.pojo.UserRole;
import com.ou.journal.repository.UserRepositoryJPA;
import com.ou.journal.service.interfaces.ManuscriptService;
import com.ou.journal.repository.UserRoleRepositoryJPA;
import com.ou.journal.service.interfaces.RoleService;
import com.ou.journal.service.interfaces.UserService;


@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepositoryJPA userRepositoryJPA;

    @Autowired
    private UserRoleRepositoryJPA userRoleRepositoryJPA;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ManuscriptService manuscriptService;

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

    @Override
    public List<Object[]> listUser() {
        return userRepositoryJPA.listUser();
    }

    @Override
    public User create(User user) {
        user.setCreatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        user.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        return userRepositoryJPA.save(user);
    }

    @Override
    public List<User> findByRoleName(String roleName) {
        return userRepositoryJPA.findByRoleName(roleName);
    }

    @Override
    public List<User> findReviewerByOlderManuscript(Long articleId) {
        Manuscript lastestManuscript = manuscriptService.getLastestManuscript(articleId);
        return userRepositoryJPA.findReviewerByOlderManuscript(articleId, lastestManuscript.getId());
    }
    
}
