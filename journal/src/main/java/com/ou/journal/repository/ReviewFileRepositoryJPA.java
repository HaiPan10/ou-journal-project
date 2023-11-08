package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.ReviewFile;

public interface ReviewFileRepositoryJPA extends JpaRepository<ReviewFile, Long> {
    
}
