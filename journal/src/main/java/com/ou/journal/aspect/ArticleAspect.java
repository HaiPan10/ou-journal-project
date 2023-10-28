package com.ou.journal.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ou.journal.enums.RoleName;
import com.ou.journal.pojo.Article;
import com.ou.journal.service.interfaces.UserRoleService;

@Aspect
@Component
public class ArticleAspect {
    // @Autowired
    // private AccountService accountService;
    // @Autowired
    // private AuthorArticleRepositoryJPA authorArticleRepositoryJPA;
    // @Autowired
    // private AuthorTypeService authorTypeService;
    // @Autowired
    // private AuthorRoleRepositoryJPA authorRoleRepositoryJPA;
    // @Autowired
    // private UserRoleRepositoryJPA userRoleRepositoryJPA;
    // @Autowired
    // private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;

    @AfterReturning(pointcut = "execution(com.ou.journal.pojo.Article com.ou.journal.service.interfaces.ArticleService.create(com.ou.journal.pojo.Article, org.springframework.web.multipart.MultipartFile))", returning = "article")
    public void addAuthorRole(Article article) throws Exception {
        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();
        // UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Account account = accountService.findByUserName(userDetails.getUsername());

        // Optional<AuthorArticle> authorArticleOptional =
        // authorArticleRepositoryJPA.findByArticleAndUser(article.getId(),
        // account.getId());
        // if (authorArticleOptional.isPresent()) {
        // AuthorArticle authorArticle = authorArticleOptional.get();
        // AuthorRole authorRole = new AuthorRole(authorArticle,
        // authorTypeService.findByType(AuthorType.CORRESPONDING_AUTHOR.toString()));
        // authorRoleRepositoryJPA.save(authorRole);
        // } else {
        // AuthorArticle authorArticle = new AuthorArticle(account.getUser(), article);
        // authorArticle.setAuthorRoles(new ArrayList<AuthorRole>(
        // Arrays.asList(
        // new AuthorRole(authorArticle,
        // authorTypeService.findByType(AuthorType.CORRESPONDING_AUTHOR.toString()))
        // )
        // ));
        // authorArticleRepositoryJPA.save(authorArticle);
        // }

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
}
