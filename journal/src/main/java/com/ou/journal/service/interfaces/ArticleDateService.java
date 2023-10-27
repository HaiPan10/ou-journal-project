package com.ou.journal.service.interfaces;

import com.ou.journal.pojo.Article;

public interface ArticleDateService {
    void addOrUpdate(Article article, String dateType) throws Exception;
}
