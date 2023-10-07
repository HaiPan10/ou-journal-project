package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.AuthorType;
import java.util.Optional;


public interface AuthorTypeRepositoryJPA extends JpaRepository<AuthorType, Long> {
    Optional<AuthorType> findByType(String type);
}
