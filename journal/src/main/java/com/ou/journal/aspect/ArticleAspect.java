package com.ou.journal.aspect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.ou.journal.enums.AuthorType;
import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.AuthorArticle;
import com.ou.journal.pojo.AuthorRole;
import com.ou.journal.repository.AuthorArticleRepositoryJPA;
import com.ou.journal.repository.AuthorRoleRepositoryJPA;
import com.ou.journal.service.interfaces.AccountService;
import com.ou.journal.service.interfaces.AuthorTypeService;

@Aspect
@Component
public class ArticleAspect {
    @Autowired
    private AccountService accountService;
    @Autowired
    private AuthorArticleRepositoryJPA authorArticleRepositoryJPA;
    @Autowired
    private AuthorTypeService authorTypeService;
    @Autowired
    private AuthorRoleRepositoryJPA authorRoleRepositoryJPA;

    @AfterReturning(
        pointcut = "execution(com.ou.journal.pojo.Article com.ou.journal.service.interfaces.ArticleService.create(com.ou.journal.pojo.Article, org.springframework.web.multipart.MultipartFile))",
        returning = "article"
    )    
    public void addCoresponddingAuthorRole(Article article) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Account account = accountService.findByUserName(userDetails.getUsername());

        System.out.println(account.getEmail());

        Optional<AuthorArticle> authorArticleOptional = authorArticleRepositoryJPA.findByArticleAndUser(article.getId(), account.getId());
        if (authorArticleOptional.isPresent()) {
            AuthorArticle authorArticle = authorArticleOptional.get();
            AuthorRole authorRole = new AuthorRole(authorArticle, authorTypeService.findByType(AuthorType.CORRESPONDING_AUTHOR.toString()));
            authorRoleRepositoryJPA.save(authorRole);
        } else {
            AuthorArticle authorArticle = new AuthorArticle(account.getUser(), article);
            authorArticle.setAuthorRoles(new ArrayList<AuthorRole>(
                Arrays.asList(
                    new AuthorRole(authorArticle, authorTypeService.findByType(AuthorType.CORRESPONDING_AUTHOR.toString()))
                )
            ));
            authorArticleRepositoryJPA.save(authorArticle);
        }
    }
}
