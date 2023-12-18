package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.ArticleCategory;


public interface ArticleCategoryRepositoryJPA extends JpaRepository<ArticleCategory, Long> {
}
