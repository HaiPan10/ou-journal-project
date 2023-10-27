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
import com.ou.journal.enums.AuthorType;
import com.ou.journal.enums.DateTypeName;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ArticleDate;
import com.ou.journal.pojo.ArticleNote;
import com.ou.journal.pojo.AuthorArticle;
import com.ou.journal.pojo.Manuscript;
import com.ou.journal.repository.ArticleRepositoryJPA;
import com.ou.journal.repository.AuthorArticleRepositoryJPA;
import com.ou.journal.repository.AuthorRoleRepositoryJPA;
import com.ou.journal.repository.ReviewArticleRepositoryJPA;
import com.ou.journal.service.interfaces.ArticleNoteService;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.DateTypeService;
import com.ou.journal.service.interfaces.MailService;
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
    @Autowired
    private AuthorArticleRepositoryJPA authorArticleRepositoryJPA;
    @Autowired
    private AuthorRoleRepositoryJPA authorRoleRepositoryJPA;
    @Autowired
    private MailService mailService;
    
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
            if (status.equals(ArticleStatus.INVITING_REVIEWER.toString())) {
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
    public void updateArticleStatus(Long articleId, Article article) throws Exception {
        Article persistArticle = retrieve(articleId);
        if (persistArticle.getStatus().equals(ArticleStatus.PENDING.toString())) {
            String status = article.getStatus();
            persistArticle.setStatus(status);
            if(status.equals(ArticleStatus.INVITING_REVIEWER.toString())){
                persistArticle.setTotalReviewer(article.getTotalReviewer());
            }
            articleRepositoryJPA.save(persistArticle);
            ArticleNote articleNote = article.getArticleNote();
            articleNoteService.createOrUpdate(articleNote, persistArticle);
            mailService.sendSecretaryVerificationmail(persistArticle, articleNote);
        }
    }

    @Override
    public List<Article> findByAuthorId(Long authorId) {
        return articleRepositoryJPA.findByAuthorId(authorId);
    }

    @Override
    public Article endInvitationReview(Long articleId) throws Exception {
        Article persistArticle = retrieve(articleId);
        Integer acceptedReviewTotal = reviewArticleRepositoryJPA.countAcceptedReview(articleId);
        if (acceptedReviewTotal > 0) {
            persistArticle.setStatus(ArticleStatus.IN_REVIEW.toString());
            return articleRepositoryJPA.save(persistArticle);
        } else {
            throw new Exception("Bài báo này chưa có reviewer nào!");
        }
    }

    @Override
    public Article editorDecide(Long articleId, String status) throws Exception {
        Article article = retrieve(articleId);
        if (article.getStatus().equals(ArticleStatus.DECIDING.toString())) {
            if (status.equals(ArticleStatus.ACCEPT.toString()) || status.equals(ArticleStatus.REJECT.toString())) {
                article.setStatus(status);
                return articleRepositoryJPA.save(article);
            } else {
                throw new Exception("Trạng thái chuyển đổi không hợp lệ!");
            }
        } else {
            throw new Exception("Trạng thái bài báo không hợp lệ!");
        }
    }

    @Override
    public Article widthdrawArticle(Long articleId, Long userId) throws Exception {
        try {
            Optional<AuthorArticle> authorArticleOptional = authorArticleRepositoryJPA.findByArticleAndUser(articleId, userId);
            if (authorArticleOptional.isPresent()) {
                AuthorArticle authorArticle = authorArticleOptional.get();
                if (authorRoleRepositoryJPA.findByAuthorArticleAndAuthorType(authorArticle.getId(),
                 AuthorType.CORRESPONDING_AUTHOR.toString()).isPresent()) {
                    Article article = retrieve(articleId);
                    article.setStatus(ArticleStatus.WITHDRAW.toString());
                    return articleRepositoryJPA.save(article);
                } else {
                    throw new Exception("Bạn không có quyền rút bài báo này!");
                }
            } else {
                throw new Exception("Bạn không có tên trong danh sách tác giả bài báo này!");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
