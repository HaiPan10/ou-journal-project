package com.ou.journal.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ArticleDate;
import com.ou.journal.repository.ArticleDateRepositoryJPA;
import com.ou.journal.service.interfaces.ArticleDateService;
import com.ou.journal.service.interfaces.DateTypeService;

@Service
@Transactional
public class ArticleDateServiceImpl implements ArticleDateService {
    @Autowired
    private ArticleDateRepositoryJPA articleDateRepositoryJPA;

    @Autowired
    private DateTypeService dateTypeService;

    @Override
    public void addOrUpdate(Article article, String dateType) throws Exception {
        Optional<ArticleDate> articleDateOptional = articleDateRepositoryJPA.
            findByArticleAndDateType(article, dateTypeService.retrieve(dateType));
            ArticleDate articleDate;
            if (articleDateOptional.isPresent()) {
                articleDate = articleDateOptional.get();
                articleDate.setDate(new Date());
                articleDateRepositoryJPA.save(articleDate);
            } else {
                articleDate = new ArticleDate(dateTypeService
                    .retrieve(dateType),
                    article, new Date());
            }
            articleDateRepositoryJPA.save(articleDate);
    }

    @Override
    public List<ArticleDate> getArticleDates(Long articleId) {
        Date submittedDate = articleDateRepositoryJPA.getSubmittedDate(articleId);
        return articleDateRepositoryJPA.getArticleDates(articleId, submittedDate);
    }
    
}
