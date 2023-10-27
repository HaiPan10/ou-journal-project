package com.ou.journal.service.interfaces;

import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ArticleNote;
import com.ou.journal.pojo.MailRequest;
import com.ou.journal.pojo.ReviewArticle;

public interface MailService {
    void sendEmail(MailRequest mailRequest);
    void sendVerificationMail(Account account);
    void sendInvitationMail(ReviewArticle reviewArticle);
    void sendSecretaryVerificationmail(Article article, ArticleNote articleNote);
}