package com.ou.journal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.AuthorNote;
import com.ou.journal.repository.AuthorNoteRepositoryJPA;
import com.ou.journal.service.interfaces.AuthorNoteService;

@Service
public class AuthorNoteServiceImpl implements AuthorNoteService{
    @Autowired
    private AuthorNoteRepositoryJPA authorNoteRepositoryJPA;

    @Override
    public AuthorNote create(AuthorNote authorNote, Article article) {
        String note = authorNote.getNote().trim();
        if (note != null && !note.isEmpty()) {
            authorNote.setNote(note);
        } else {
            return null;
        }

        AuthorNote persistAuthorNote = authorNoteRepositoryJPA.findById(article.getId()).orElse(null);
        if(persistAuthorNote != null){
            // update
            persistAuthorNote.setNote(authorNote.getNote());
            return authorNoteRepositoryJPA.save(persistAuthorNote);
        }

        // create
        authorNote.setArticle(article);
        return authorNoteRepositoryJPA.save(authorNote);
    }

    @Override
    public AuthorNote retrieve(Long authorNoteId) throws Exception {
        return authorNoteRepositoryJPA.findById(authorNoteId)
            .orElseThrow(() -> new Exception("Không tìm thấy Note của tác giả"));
    }
    
}
