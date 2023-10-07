package com.ou.journal.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.pojo.Article;

public interface ArticleService {
    Article create(Article article, MultipartFile file) throws Exception;
}
