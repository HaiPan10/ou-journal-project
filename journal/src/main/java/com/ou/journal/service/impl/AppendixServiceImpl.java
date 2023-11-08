package com.ou.journal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ou.journal.pojo.Appendix;
import com.ou.journal.repository.AppendixRepositoryJPA;
import com.ou.journal.service.interfaces.AppendixService;

@Service
public class AppendixServiceImpl implements AppendixService {
    @Autowired
    private AppendixRepositoryJPA appendixRepositoryJPA;

    @Override
    public Appendix create(Appendix appendix) throws Exception {
        try {
            return appendixRepositoryJPA.save(appendix);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
}
