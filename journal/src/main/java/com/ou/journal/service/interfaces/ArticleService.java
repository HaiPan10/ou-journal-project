package com.ou.journal.service.interfaces;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ArticleNote;

public interface ArticleService {
    Article create(Article article, MultipartFile file) throws Exception;
    List<Article> list(String status);
    Article retrieve(Long articleId) throws Exception;
    void updateArticleStatus(Long articleId, ArticleNote articleNote, String status) throws Exception;
}
