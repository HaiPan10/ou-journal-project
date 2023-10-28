package com.ou.journal.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.enums.DateTypeName;
import com.ou.journal.enums.ReviewArticleStatus;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ReviewArticle;
import com.ou.journal.repository.ArticleRepositoryJPA;
import com.ou.journal.repository.ReviewArticleRepositoryJPA;
import com.ou.journal.service.interfaces.ArticleDateService;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.MailService;

@Aspect
@Component
public class ReviewArticleAspect {
    @Autowired
    private MailService mailService;
    @Autowired
    private ReviewArticleRepositoryJPA reviewArticleRepositoryJPA;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleRepositoryJPA articleRepositoryJPA;
    @Autowired
    private ArticleDateService articleDateService;

    @AfterReturning(
        pointcut = "execution(com.ou.journal.pojo.ReviewArticle com.ou.journal.service.interfaces.ReviewArticleService.create(com.ou.journal.pojo.User, com.ou.journal.pojo.Article))",
        returning = "reviewArticle"
    )
    public void sendInvitationEmail(ReviewArticle reviewArticle) throws Exception {
        mailService.sendInvitationMail(reviewArticle);
    }

    @AfterReturning(
        pointcut = "execution(com.ou.journal.pojo.ReviewArticle com.ou.journal.service.interfaces.ReviewArticleService.changeReviewStatus(Long, String, String, Long))",
        returning = "reviewArticle"
    )
    public void autoChangeStatus(ReviewArticle reviewArticle) throws Exception {
        Article article = articleService.retrieve(reviewArticle.getArticle().getId());
        if (reviewArticle.getStatus().equals(ReviewArticleStatus.ACCEPTED.toString()) &&
        article.getTotalReviewer().equals(reviewArticleRepositoryJPA.countAcceptedReview(article.getId()))
        && article.getStatus().equals(ArticleStatus.INVITING_REVIEWER.toString())) {
            article.setStatus(ArticleStatus.IN_REVIEW.toString());
            articleRepositoryJPA.save(article);
            mailService.sendInReviewStatusChangeMail(article);
        }
    }

    @AfterReturning(
        pointcut = "execution(com.ou.journal.pojo.ReviewArticle com.ou.journal.service.interfaces.ReviewArticleService.acceptReviewAndCreateAccount(Long, String, Long, com.ou.journal.pojo.Account))",
        returning = "reviewArticle"
    )
    public void autoChangeAcceptStatus(ReviewArticle reviewArticle) throws Exception {
        Article article = articleService.retrieve(reviewArticle.getArticle().getId());
        if (reviewArticle.getStatus().equals(ReviewArticleStatus.ACCEPTED.toString()) &&
        article.getTotalReviewer().equals(reviewArticleRepositoryJPA.countAcceptedReview(article.getId()))
        && article.getStatus().equals(ArticleStatus.INVITING_REVIEWER.toString())) {
            article.setStatus(ArticleStatus.IN_REVIEW.toString());
            articleDateService.addOrUpdate(article, DateTypeName.IN_REVIEW_DATE.toString());
            articleRepositoryJPA.save(article);
            mailService.sendInReviewStatusChangeMail(article);
        }
    }

    @AfterReturning(
        pointcut = "execution(com.ou.journal.pojo.ReviewArticle com.ou.journal.service.interfaces.ReviewArticleService.doneReview(Long, Long))",
        returning = "reviewArticle"
    )
    public void autoChangeDecidingStatus(ReviewArticle reviewArticle) throws Exception {
        if (reviewArticleRepositoryJPA.countAcceptedReview(reviewArticle.getArticle().getId()) == 0) {
            Article article = articleService.retrieve(reviewArticle.getArticle().getId());
            if (article.getStatus().equals(ArticleStatus.IN_REVIEW.toString())) {
                article.setStatus(ArticleStatus.DECIDING.toString());
                articleDateService.addOrUpdate(article, DateTypeName.DECIDING_DATE.toString());
                articleRepositoryJPA.save(article);
            }
        }
    }
}
