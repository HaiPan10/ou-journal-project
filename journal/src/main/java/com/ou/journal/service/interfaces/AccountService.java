package com.ou.journal.service.interfaces;

import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.AuthRequest;
import com.ou.journal.pojo.AuthResponse;

public interface AccountService {
    Account create(Account account) throws Exception;
    boolean changeAccountStatus(Long accountId, String status) throws Exception;
    List<Account> findAll();
    Account retrieve(Long accountId) throws Exception;
    AuthResponse login(AuthRequest account) throws AccountNotFoundException, Exception;
}
