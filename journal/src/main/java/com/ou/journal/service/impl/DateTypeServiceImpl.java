package com.ou.journal.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ou.journal.pojo.DateType;
import com.ou.journal.repository.DateTypeRepositoryJPA;
import com.ou.journal.service.interfaces.DateTypeService;

@Service
public class DateTypeServiceImpl implements DateTypeService{
    @Autowired
    private DateTypeRepositoryJPA dateTypeRepositoryJPA;

    @Override
    public DateType retrieve(String typeName) throws Exception {
        Optional<DateType> dateTypeOptional = dateTypeRepositoryJPA.findByTypeName(typeName);
        if (dateTypeOptional.isPresent()) {
            return dateTypeOptional.get();
        } else {
            throw new Exception("Date type không hợp lệ!");
        }
    }
}
