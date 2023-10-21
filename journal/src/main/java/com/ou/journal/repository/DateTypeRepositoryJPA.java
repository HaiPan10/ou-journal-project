package com.ou.journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ou.journal.pojo.DateType;
import java.util.Optional;

public interface DateTypeRepositoryJPA extends JpaRepository<DateType, Long> {
    Optional<DateType>  findByTypeName(String typeName);
}
