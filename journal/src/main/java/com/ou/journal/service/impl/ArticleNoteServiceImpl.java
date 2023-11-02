package com.ou.journal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ArticleNote;
import com.ou.journal.repository.ArticleNoteRepositoryJPA;
import com.ou.journal.service.interfaces.ArticleNoteService;

@Service
public class ArticleNoteServiceImpl implements ArticleNoteService{

    @Autowired
    private ArticleNoteRepositoryJPA articleNoteRepositoryJPA;

    @Override
    public ArticleNote createOrUpdate(ArticleNote articleNote, Article article) {
        System.out.println("ARTICLE NOTE: " + articleNote.getNote());
        System.out.println("THE ARTICLE ID: " + article.getId());
        ArticleNote persistArticleNote = articleNoteRepositoryJPA.findById(article.getId()).orElse(null);
        if(persistArticleNote != null){
            // update
            System.out.println("UPDATING!!!");
            persistArticleNote.setNote(articleNote.getNote());
            return articleNoteRepositoryJPA.save(persistArticleNote);
        }

        // create
        System.out.println("CREATING!!!");
        articleNote.setArticle(article);
        return articleNoteRepositoryJPA.save(articleNote);
    }

    @Override
    public ArticleNote retrieve(Long id) throws Exception {
        return articleNoteRepositoryJPA.findById(id)
            .orElseThrow(() -> new Exception("Không tìm thấy Note"));
    }
    
}
