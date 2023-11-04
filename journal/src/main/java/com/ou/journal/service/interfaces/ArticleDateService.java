package com.ou.journal.service.interfaces;

import java.util.List;

import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ArticleDate;

public interface ArticleDateService {
    void addOrUpdate(Article article, String dateType) throws Exception;
    List<ArticleDate> getArticleDates(Long articleId);
}
