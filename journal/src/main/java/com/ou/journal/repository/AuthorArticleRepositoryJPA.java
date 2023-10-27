package com.ou.journal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ou.journal.pojo.AuthorArticle;
import com.ou.journal.pojo.User;

public interface AuthorArticleRepositoryJPA extends JpaRepository<AuthorArticle, Long> {
    @Query("FROM AuthorArticle a WHERE a.article.id = ?1 AND a.user.id = ?2 ")
    Optional<AuthorArticle> findByArticleAndUser(Long articleId, Long userId);

    @Query("SELECT a.user FROM AuthorArticle a JOIN a.authorRoles r " +
    "WHERE a.article.id = ?1 AND r.authorType.type = ?2")
    Optional<User> findByAnyRoleInAuthourArticle(Long articleId, String authorRole);
}
