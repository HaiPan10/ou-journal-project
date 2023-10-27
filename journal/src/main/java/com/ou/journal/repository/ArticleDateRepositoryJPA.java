package com.ou.journal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.ArticleDate;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.DateType;



public interface ArticleDateRepositoryJPA extends JpaRepository<ArticleDate, Long> {
    Optional<ArticleDate> findByArticleAndDateType(Article article, DateType dateType);
}