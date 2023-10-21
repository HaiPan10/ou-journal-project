package com.ou.journal.service.interfaces;

import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.MailRequest;

public interface MailService {
    void sendEmail(MailRequest mailRequest);
    void sendVerificationMail(Account account);
}