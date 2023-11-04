package com.ou.journal.service.interfaces;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ArticleNote;
import com.ou.journal.pojo.User;

public interface ArticleService {
    Article create(Article article, MultipartFile file) throws Exception;
    List<Article> list(String status);
    List<Article> list(String status, Long editorId);
    List<Article> getArticleWaitingForInviteReviewer(Long editorId);
    List<Article> getArticleWaitingForAcceptFromReviewer(Long editorId);
    List<Article> getInReviewArticles(Long editorId);
    List<Article> getReviewedArticles(Long editorId);
    Long countArticleWaitingAssignEditor();
    Long countAssignedArticleById(Long editorId);
    Article retrieve(Long articleId) throws Exception;
    Article retrieve(Long articleId, Long userId) throws Exception;
    // Article endInvitationReview(Long articleId) throws Exception;
    void secretaryDecide(Long articleId, Article article) throws Exception;
    List<Article> findByAuthorId(Long authorId);
    Article editorDecide(Long articleId, String status, ArticleNote articleNote) throws Exception;
    Article widthdrawArticle(Long articleId, Long userId) throws Exception;
    Article assignEditor(Long articleId, Long userId) throws Exception;
    Article assignEditor(Long articleId, String email) throws Exception;
    Article assignEditor(Long articleId, User user) throws Exception;
    String getArticleStatusById(Long articleId);
    List<Article> findByEditorUserId(Long editorId);
}
