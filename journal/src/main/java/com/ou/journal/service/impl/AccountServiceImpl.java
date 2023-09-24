package com.ou.journal.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.User;
import com.ou.journal.repository.AccountRepositoryJPA;
import com.ou.journal.repository.UserRepositoryJPA;
import com.ou.journal.service.interfaces.AccountService;
import com.ou.journal.service.interfaces.UserService;

import jakarta.transaction.Transactional;

@Service
@Transactional(rollbackOn = Exception.class)
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepositoryJPA accountRepositoryJPA;

    @Autowired UserRepositoryJPA userRepositoryJPA;

    @Autowired
    private UserService userService;

    @Override
    // đăng ký cho Author User
    public Account create(Account account) throws Exception {
        try {
            if (accountRepositoryJPA.findByEmail(account.getEmail()).isPresent()) {
                throw new Exception(String.format("Email %s đã tồn tại!", account.getEmail()));
            }
            userService.createAuthorUser(account.getUser());
            account.setCreatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            account.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            accountRepositoryJPA.save(account);
            return account;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
}
