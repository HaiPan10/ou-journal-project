package com.ou.journal.service.interfaces;

import java.util.List;

import com.ou.journal.pojo.Account;

public interface AccountService {
    Account create(Account account) throws Exception;
    boolean changeAccountStatus(Long accountId, String status) throws Exception;
    List<Account> findAll();
    Account retrieve(Long accountId) throws Exception;
}
