package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.EditorFile;
import java.util.Optional;


public interface EditorFileRepositoryJPA extends JpaRepository<EditorFile, Long>  {
    Optional<EditorFile> findById(Long id);
}
