package com.ou.journal.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ou.journal.pojo.AuthorType;
import com.ou.journal.repository.AuthorTypeRepositoryJPA;
import com.ou.journal.service.interfaces.AuthorTypeService;

@Service
public class AuthorTypeServiceImpl implements AuthorTypeService {
    @Autowired
    private AuthorTypeRepositoryJPA authorTypeRepositoryJPA;
    @Override
    public AuthorType findByType(String type) throws Exception {
        Optional<AuthorType> authorTypeOptional = authorTypeRepositoryJPA.findByType(type);
        if (authorTypeOptional.isPresent()) {
            return authorTypeOptional.get();
        } else {
            throw new Exception("Author type không hợp lệ!");
        }
    }
    
}
