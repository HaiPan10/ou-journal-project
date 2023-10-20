package com.ou.journal.service.interfaces;

import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ArticleNote;

public interface ArticleNoteService {
    ArticleNote createOrUpdate(ArticleNote articleNote, Article article);
    ArticleNote retrieve(Long id) throws Exception;
}
