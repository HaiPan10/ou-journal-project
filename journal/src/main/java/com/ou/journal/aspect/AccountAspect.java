package com.ou.journal.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ou.journal.pojo.Account;
import com.ou.journal.service.interfaces.MailService;

@Aspect
@Component
public class AccountAspect {
    @Autowired
    private MailService mailService;

    @AfterReturning(
        pointcut = "execution(com.ou.journal.pojo.Account com.ou.journal.service.interfaces.AccountService.create(com.ou.journal.pojo.Account))",
        returning = "account"
    )
    public void sendVerificationEmail(Account account) throws Exception {
        // mailService.sendVerificationMail(account);
    }
}
