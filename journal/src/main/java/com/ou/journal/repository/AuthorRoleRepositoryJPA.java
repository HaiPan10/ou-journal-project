package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ou.journal.pojo.AuthorRole;
import java.util.Optional;


public interface AuthorRoleRepositoryJPA extends JpaRepository<AuthorRole, Long> {
    @Query("FROM AuthorRole a WHERE a.authorArticle.id = ?1 AND a.authorType.type = ?2 ")
    Optional<AuthorRole> findByAuthorArticleAndAuthorType(Long authorArticleId, String roleName);
}
