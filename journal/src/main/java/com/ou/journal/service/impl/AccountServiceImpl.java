package com.ou.journal.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ou.journal.enums.AccountStatus;
import com.ou.journal.pojo.Account;
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

    @Autowired
    UserRepositoryJPA userRepositoryJPA;

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
            account.setStatus(AccountStatus.PENDING.toString());
            accountRepositoryJPA.save(account);
            return account;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public boolean changeAccountStatus(Long accountId, String status) throws Exception {
        try {
            Account account = retrieve(accountId);
            account.setStatus(status);
            accountRepositoryJPA.save(account);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public List<Account> findAll() {
        return accountRepositoryJPA.findAll();
    }

    @Override
    public Account retrieve(Long accountId) throws Exception {
        Optional<Account> accountOptional = accountRepositoryJPA.findById(accountId);
        if (accountOptional.isPresent()) {
            return accountOptional.get();
        } else {
            throw new Exception("Tài khoản không tồn tại!");
        }
    }

    @Override
    public Account findByUserName(String userName) throws Exception {
        Optional<Account> accountOptional = accountRepositoryJPA.findByUserName(userName);
        if (accountOptional.isPresent()) {
            return accountOptional.get();
        } else {
            throw new Exception("Tài khoản không tồn tại!");
        }
    }
}
