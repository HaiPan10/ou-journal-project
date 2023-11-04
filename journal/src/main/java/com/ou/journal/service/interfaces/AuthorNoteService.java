package com.ou.journal.service.interfaces;

import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.AuthorNote;

public interface AuthorNoteService {
    AuthorNote create(AuthorNote authorNote, Article article);
    AuthorNote retrieve(Long authorNoteId) throws Exception;
}
