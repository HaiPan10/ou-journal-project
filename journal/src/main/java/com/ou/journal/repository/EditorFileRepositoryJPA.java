package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.EditorFile;

public interface EditorFileRepositoryJPA extends JpaRepository<EditorFile, Long>  {
    
}
