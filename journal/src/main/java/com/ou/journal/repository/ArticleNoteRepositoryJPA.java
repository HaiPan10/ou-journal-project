package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.ArticleNote;

public interface ArticleNoteRepositoryJPA extends JpaRepository<ArticleNote, Long>{
    
}
