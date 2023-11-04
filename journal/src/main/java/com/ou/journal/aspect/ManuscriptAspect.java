package com.ou.journal.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.enums.DateTypeName;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.Manuscript;
import com.ou.journal.repository.ArticleRepositoryJPA;
import com.ou.journal.service.interfaces.ArticleDateService;

@Aspect
@Component
public class ManuscriptAspect {
    @Autowired
    private ArticleDateService articleDateService;

    @Autowired
    private ArticleRepositoryJPA articleRepositoryJPA;

    @AfterReturning(
        pointcut = "execution(com.ou.journal.pojo.Manuscript com.ou.journal.service.interfaces.ManuscriptService.reUpManuscript(Long, org.springframework.web.multipart.MultipartFile, com.ou.journal.pojo.AuthorNote))",
        returning = "manuscript"
    )
    public void updateSubmitedDate(Manuscript manuscript) throws Exception {
        Article article = manuscript.getArticle();
        article.setStatus(ArticleStatus.PENDING.toString());
        articleRepositoryJPA.save(article);
        articleDateService.addOrUpdate(manuscript.getArticle(), DateTypeName.SUBMITTED_DATE.toString());
    }
}
