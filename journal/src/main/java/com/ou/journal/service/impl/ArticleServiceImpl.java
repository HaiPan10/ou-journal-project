package com.ou.journal.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.enums.DateTypeName;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ArticleDate;
import com.ou.journal.pojo.Manuscript;
import com.ou.journal.repository.ArticleRepositoryJPA;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.DateTypeService;
import com.ou.journal.service.interfaces.UserService;

import jakarta.transaction.Transactional;

@Service
@Transactional(rollbackOn = Exception.class)
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleRepositoryJPA articleRepositoryJPA;
    @Autowired
    private DateTypeService dateTypeService;
    @Autowired
    private UserService userService;
    
    @Override
    public Article create(Article article, MultipartFile file) throws Exception {
        try {
            article.setStatus(ArticleStatus.PENDING.toString());
            article.setArticleDates(
                    new ArrayList<ArticleDate>(
                            Arrays.asList(
                                    new ArticleDate(
                                            dateTypeService.retrieve(DateTypeName.SUBMITTED_DATE.toString()),
                                            article,
                                            new Date()))));
            article.setManuscripts(
                    new ArrayList<Manuscript>(
                            Arrays.asList(
                                    new Manuscript(
                                            file.getBytes(),
                                            file.getSize(),
                                            "1.0",
                                            new Date(),
                                            file.getContentType(),
                                            article))));
            
            article.getAuthorArticles().forEach(authorArticle -> {
                authorArticle.setArticle(article);
                if (authorArticle.getUser().getId() == null) {
                    try {
                        authorArticle.setUser(userService.createAuthorUser(authorArticle.getUser()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                authorArticle.getAuthorRoles().forEach(authorRole -> {
                    authorRole.setAuthorArticle(authorArticle);
                });
            });
            return articleRepositoryJPA.save(article);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
