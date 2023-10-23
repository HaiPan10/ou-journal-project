package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.AuthorRole;

public interface AuthorRoleRepositoryJPA extends JpaRepository<AuthorRole, Long> {
}
