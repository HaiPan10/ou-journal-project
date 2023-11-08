package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.Appendix;

public interface AppendixRepositoryJPA extends JpaRepository<Appendix, Long> {
    
}
