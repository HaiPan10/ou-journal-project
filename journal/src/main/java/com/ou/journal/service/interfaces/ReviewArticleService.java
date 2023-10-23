package com.ou.journal.service.interfaces;

import java.util.List;

import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ReviewArticle;
import com.ou.journal.pojo.User;

public interface ReviewArticleService {
    ReviewArticle create(User user, Article article) throws Exception;
    List<ReviewArticle> findByArticle(Article article);
    ReviewArticle changeReviewStatus(Long reviewArticleId, String status, String email, Long userId) throws Exception;
    boolean checkArticleAvailable(Long reviewArticleId, String email, Long userId) throws Exception;
    ReviewArticle acceptReviewAndCreateAccount(Long reviewArticleId, String email, Long userId, Account account) throws Exception;
    boolean checkResponseRevviewArticleInvitation(Long reviewArticleId, Long userId, String email);
}
