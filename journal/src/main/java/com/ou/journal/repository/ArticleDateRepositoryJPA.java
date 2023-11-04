package com.ou.journal.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ou.journal.pojo.ArticleDate;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.DateType;



public interface ArticleDateRepositoryJPA extends JpaRepository<ArticleDate, Long> {
    Optional<ArticleDate> findByArticleAndDateType(Article article, DateType dateType);
    @Query("FROM ArticleDate a WHERE a.article.id = ?1 AND a.date >= ?2")
    List<ArticleDate> getArticleDates(Long articleId, Date date);
    @Query("SELECT a.date FROM ArticleDate a WHERE a.article.id = ?1 AND a.dateType.typeName = 'SUBMITTED_DATE'")
    Date getSubmittedDate(Long articleId);
}