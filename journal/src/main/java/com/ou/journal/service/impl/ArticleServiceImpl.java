package com.ou.journal.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.enums.DateTypeName;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ArticleDate;
import com.ou.journal.pojo.ArticleNote;
import com.ou.journal.pojo.Manuscript;
import com.ou.journal.repository.ArticleRepositoryJPA;
import com.ou.journal.repository.ReviewArticleRepositoryJPA;
import com.ou.journal.service.interfaces.ArticleNoteService;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.DateTypeService;
import com.ou.journal.service.interfaces.ManuscriptService;
import com.ou.journal.service.interfaces.UserService;


@Service
@Transactional(rollbackFor = Exception.class)
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleRepositoryJPA articleRepositoryJPA;
    @Autowired
    private DateTypeService dateTypeService;
    @Autowired
    private UserService userService;
    @Autowired
    private ManuscriptService manuscriptService;
    @Autowired
    private ArticleNoteService articleNoteService;
    @Autowired
    private ReviewArticleRepositoryJPA reviewArticleRepositoryJPA;
    
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
                        authorArticle.setUser(userService.findByEmail(authorArticle.getUser().getEmail()));
                    } catch (Exception e) {
                        try {
                            authorArticle.setUser(userService.createAuthorUser(authorArticle.getUser()));
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
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

    @Override
    public List<Article> list(String status) {
        if (status == null) {
            return articleRepositoryJPA.list(ArticleStatus.PENDING.toString());
        } else {
            List<Article> articles = articleRepositoryJPA.list(status);
            if (status.equals(ArticleStatus.IN_REVIEW.toString())) {
                articles.forEach(article -> article.setAcceptedReviewer(
                    reviewArticleRepositoryJPA.countAcceptedReview(article.getId())));
            }
            return articles;
        }
    }

    @Override
    public Article retrieve(Long articleId) throws Exception {
        Optional<Article> articleOptional = articleRepositoryJPA.findById(articleId);
        if (articleOptional.isPresent()) {
            Manuscript manuscript = manuscriptService.getLastestManuscript(articleId);
            Article article = articleOptional.get();
            article.setCurrentManuscript(manuscript);
            return article;
        } else {
            throw new Exception("Bài báo không tồn tại!");
        }
    }

    @Override
    public void updateArticleStatus(Long articleId, Article article, String status) throws Exception {
        Article persistArticle = retrieve(articleId);
        persistArticle.setStatus(status);
        if(status.equals(ArticleStatus.ACCEPT.toString())){
            persistArticle.setTotalReviewer(article.getTotalReviewer());
        }
        articleRepositoryJPA.save(persistArticle);

        ArticleNote articleNote = article.getArticleNote();
        articleNoteService.createOrUpdate(articleNote, persistArticle);
    }

    @Override
    public Article endInvitationReview(Long articleId) throws Exception {
        Article persistArticle = retrieve(articleId);
        Integer acceptedReviewTotal = reviewArticleRepositoryJPA.countAcceptedReview(articleId);
        if (acceptedReviewTotal > 0) {
            persistArticle.setStatus(ArticleStatus.DECIDING.toString());
            return articleRepositoryJPA.save(persistArticle);
        } else {
            throw new Exception("Bài báo này chưa có reviewer nào!");
        }
    }
}
