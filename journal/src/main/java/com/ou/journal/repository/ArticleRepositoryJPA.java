package com.ou.journal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ou.journal.pojo.Article;

public interface ArticleRepositoryJPA extends JpaRepository<Article, Long> {
    @Query("FROM Article a WHERE a.status = ?1")
    List<Article> list(String status);
    @Query("FROM Article a WHERE a.status = ?1 AND a.editorUser.id = ?2")
    List<Article> list(String status, Long userId);
    Optional<Article> findById(Long id);
    @Query("SELECT a FROM Article a " + 
        "JOIN AuthorArticle aa ON a.id = aa.article.id " + 
        "WHERE aa.user.id = ?1 "
    )
    List<Article> findByAuthorId(Long authorId);
    @Query("SELECT a.status FROM Article a WHERE a.id = ?1")
    String getArticleStatus(Long articleId);
    @Query("SELECT COUNT(*) FROM Article a WHERE a.editorUser IS NULL AND a.status = ?1")
    Long countArticleWaitingAssignEditor(String status);
    @Query("FROM Article a where a.editorUser.id = ?1")
    List<Article> findByEditorUserId(Long editorUser);
    @Query("SELECT COUNT(*) FROM Article a WHERE a.editorUser.id = ?1")
    Long countAssignedArticleById(Long editorId);
}
