package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.AuthorNote;

public interface AuthorNoteRepositoryJPA extends JpaRepository<AuthorNote, Long>{
    
}
