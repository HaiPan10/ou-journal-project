package com.ou.journal.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ou.journal.enums.AuthorType;
import com.ou.journal.pojo.Article;
import com.ou.journal.service.interfaces.AuthorRoleService;

@Aspect
@Component
public class ArticleAspect {
    @Autowired
    private AuthorRoleService authorRoleService;

    @AfterReturning(
        pointcut = "execution(com.ou.journal.pojo.Article com.ou.journal.service.interfaces.ArticleService.create(com.ou.journal.pojo.Article, org.springframework.web.multipart.MultipartFile, Long))",
        returning = "article"
    )    
    public void addCoresponddingAuthorRole(Article article) throws Exception {
        authorRoleService.create(article.getAuthorArticles().get(0), AuthorType.CORRESPONDING_AUTHOR.toString());
    }
}
