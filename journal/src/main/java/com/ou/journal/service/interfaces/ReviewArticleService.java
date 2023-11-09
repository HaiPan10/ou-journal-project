package com.ou.journal.service.interfaces;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ReviewArticle;
import com.ou.journal.pojo.User;

public interface ReviewArticleService {
    ReviewArticle create(User user, Article article) throws Exception;
    List<ReviewArticle> findByArticle(Long articleId);
    ReviewArticle changeReviewStatus(Long reviewArticleId, String status, String email, Long userId) throws Exception;
    boolean checkArticleAvailable(Long reviewArticleId, String email, Long userId) throws Exception;
    ReviewArticle acceptReviewAndCreateAccount(Long reviewArticleId, String email, Long userId, Account account) throws Exception;
    boolean checkResponseRevviewArticleInvitation(Long reviewArticleId, Long userId, String email);
    List<ReviewArticle> getReviewArticles(Long userId, String reviewArticleStatus);
    Integer countReviewArticles(Long userId, String reviewArticleStatus);
    ReviewArticle doneReview(Long reviewArticle, Long userId, MultipartFile reviewFile, String reviewStatus) throws Exception;
    ReviewArticle retrieve(Long reviewArticle) throws Exception;
    ReviewArticle retrieve(Long reviewArticle, Long userId) throws Exception;
    Integer countReviewArticleByStatus(Long manuscriptId, String status);
    List<ReviewArticle> findByOlderManuscript(Long articleId);
    Integer countReviewedArticle(Long manuscriptId);
}
