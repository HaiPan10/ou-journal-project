package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ou.journal.pojo.Manuscript;
import java.util.Optional;


public interface ManuscriptRepositoryJPA  extends JpaRepository<Manuscript, Long> {
    @Query("FROM Manuscript m WHERE m.article.id = ?1 ORDER BY m.createdDate DESC LIMIT 1")
    Manuscript getLastestManuscript(Long articleId);
    Optional<Manuscript> findById(Long id);
    @Query("SELECT m.version FROM Manuscript m WHERE m.article.id = ?1 ORDER BY m.createdDate DESC LIMIT 1")
    String getLastestVersion(Long articleId);
}
