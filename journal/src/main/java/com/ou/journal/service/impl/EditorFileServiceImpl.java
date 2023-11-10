package com.ou.journal.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ou.journal.pojo.EditorFile;
import com.ou.journal.repository.EditorFileRepositoryJPA;
import com.ou.journal.service.interfaces.EditorFileService;

@Service
public class EditorFileServiceImpl implements EditorFileService {

    @Autowired
    private EditorFileRepositoryJPA editorFileRepositoryJPA;

    @Override
    public EditorFile retrieve(Long id) throws Exception {
        Optional<EditorFile> editorFileOptional = editorFileRepositoryJPA.findById(id);
        if(editorFileOptional.isPresent()) {
            return editorFileOptional.get();
        } else {
            throw new Exception("Invalid editor file!!!");
        }
    }
    
}
