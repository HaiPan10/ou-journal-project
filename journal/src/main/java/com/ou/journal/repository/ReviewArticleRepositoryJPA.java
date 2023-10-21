package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.ReviewArticle;

public interface ReviewArticleRepositoryJPA extends JpaRepository<ReviewArticle, Long> {
    
}
