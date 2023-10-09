package com.ou.journal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ou.journal.pojo.Manuscript;
import com.ou.journal.repository.ManuscriptRepositoryJPA;
import com.ou.journal.service.interfaces.ManuscriptService;

@Service
public class ManuscriptServiceImpl implements ManuscriptService {
    @Autowired
    private ManuscriptRepositoryJPA manuscriptRepositoryJPA;

    @Override
    public Manuscript getLastestManuscript(Long articleId) {
        return manuscriptRepositoryJPA.getLastestManuscript(articleId);
    }
    
}
