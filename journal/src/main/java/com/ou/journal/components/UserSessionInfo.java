package com.ou.journal.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.ou.journal.pojo.Account;
import com.ou.journal.service.interfaces.AccountService;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSessionInfo {
    private Account account = null;

    @Autowired
    private AccountService accountService;

    public Account getCurrentAccount() throws Exception {
        System.out.println("[DEBUG] - GET CURRENT ACCOUNT");
        if(SecurityContextHolder.getContext().getAuthentication() == null){
            this.account = null;
        }
        else if (account == null) {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            try {
                this.account = accountService.findByUserName(name);
            } catch (Exception e) {
                this.account = accountService.getByEmail(name).get();
            }
        }
        return this.account;
    }
}
