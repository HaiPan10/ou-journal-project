package com.ou.journal.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ou.journal.enums.RoleName;
import com.ou.journal.pojo.Article;
import com.ou.journal.service.interfaces.UserRoleService;
import com.ou.journal.repository.UserRoleRepositoryJPA;
import com.ou.journal.service.interfaces.MailService;
import com.ou.journal.service.interfaces.RoleService;

@Aspect
@Component
public class ArticleAspect {
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserRoleRepositoryJPA userRoleRepositoryJPA;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MailService mailService;

    @AfterReturning(pointcut = "execution(com.ou.journal.pojo.Article com.ou.journal.service.interfaces.ArticleService.create(com.ou.journal.pojo.Article, org.springframework.web.multipart.MultipartFile))", returning = "article")
    public void addAuthorRole(Article article) throws Exception {
        article.getAuthorArticles().forEach(authorArticle -> {
            if (!userRoleService.getByUserAndRoleName(authorArticle.getUser(),
                    RoleName.ROLE_AUTHOR.toString()).isPresent()) {
                try {
                    userRoleService.addUserRole(authorArticle.getUser(), RoleName.ROLE_AUTHOR.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @AfterReturning(
        pointcut = "execution(com.ou.journal.pojo.Article com.ou.journal.service.interfaces.ArticleService.endInvitationReview(Long))",
        returning = "article"
    )
    public void sendInReviewStatusChangeMail(Article article) throws Exception {
        mailService.sendInReviewStatusChangeMail(article);
    }
}
