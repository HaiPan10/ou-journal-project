package com.ou.journal.service.interfaces;

import java.util.List;
import java.util.Optional;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.security.access.AccessDeniedException;

import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.AuthRequest;
import com.ou.journal.pojo.AuthResponse;
import com.ou.journal.pojo.Role;
import com.ou.journal.pojo.User;

public interface AccountService {
    Account create(Account account, User user) throws Exception;
    Account create(Account account, Long userId) throws Exception;
    boolean changeAccountStatus(Long accountId, String status) throws Exception;
    List<Account> findAll();
    Account retrieve(Long accountId) throws Exception;
    AuthResponse login(AuthRequest account) throws AccountNotFoundException, Exception;
    Account findByUserName(String userName) throws Exception;
    boolean verifyEmail(Long accountId, String verificationCode) throws Exception;
    Optional<Account> getAccount(Long accountId);
    Optional<Account> getByEmail(String email) throws Exception;
    void changeRole(Role role, User user) throws AccessDeniedException, Exception;
}
