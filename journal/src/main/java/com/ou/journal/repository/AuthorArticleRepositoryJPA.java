package com.ou.journal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ou.journal.pojo.AuthorArticle;

public interface AuthorArticleRepositoryJPA extends JpaRepository<AuthorArticle, Long> {
    @Query("FROM AuthorArticle a WHERE a.article.id = ?1 AND a.user.id = ?2 ")
    Optional<AuthorArticle> findByArticleAndUser(Long articleId, Long userId);
}
