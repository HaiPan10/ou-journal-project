package com.ou.journal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ou.journal.pojo.Article;

public interface ArticleRepositoryJPA extends JpaRepository<Article, Long> {
    @Query("FROM Article a WHERE a.status = ?1")
    List<Article> list(String status);
    Optional<Article> findById(Long id);
}
