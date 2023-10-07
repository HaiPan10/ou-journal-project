package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.Article;

public interface ArticleRepositoryJPA extends JpaRepository<Article, Long> {
}
