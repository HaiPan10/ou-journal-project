package com.ou.journal.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.enums.AuthorType;
import com.ou.journal.enums.DateTypeName;
import com.ou.journal.enums.ReviewArticleStatus;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ArticleDate;
import com.ou.journal.pojo.ArticleNote;
import com.ou.journal.pojo.AuthorArticle;
import com.ou.journal.pojo.AuthorNote;
import com.ou.journal.pojo.Manuscript;
import com.ou.journal.pojo.User;
import com.ou.journal.repository.ArticleRepositoryJPA;
import com.ou.journal.repository.AuthorArticleRepositoryJPA;
import com.ou.journal.repository.AuthorRoleRepositoryJPA;
import com.ou.journal.repository.ReviewArticleRepositoryJPA;
import com.ou.journal.service.interfaces.ArticleDateService;
import com.ou.journal.service.interfaces.ArticleNoteService;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.AuthorNoteService;
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
    @Autowired
    private ArticleDateService articleDateService;
    @Autowired
    private AuthorNoteService authorNoteService;

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

            //Hai: save the author note when submit new article
            AuthorNote authorNote = article.getAuthorNote();
            article.setAuthorNote(null);
            Article persistArticle = articleRepositoryJPA.save(article);
            authorNoteService.create(authorNote, persistArticle);

            return persistArticle;
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
            // if (status.equals(ArticleStatus.INVITING_REVIEWER.toString())) {
            // articles.forEach(article -> article.setAcceptedReviewer(
            // reviewArticleRepositoryJPA.countAcceptedReview(article.getId())));
            // }
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
    public void secretaryDecide(Long articleId, Article article) throws Exception {
        Article persistArticle = retrieve(articleId);
        if (persistArticle.getStatus().equals(ArticleStatus.PENDING.toString())) {
            String status = article.getStatus();
            persistArticle.setStatus(status);
            // if(status.equals(ArticleStatus.INVITING_REVIEWER.toString())){
            // persistArticle.setTotalReviewer(article.getTotalReviewer());
            // }
            try {
                articleDateService.addOrUpdate(persistArticle, DateTypeName.SECRETARY_VIEWED_DATE.toString());
                articleRepositoryJPA.save(persistArticle);
                ArticleNote articleNote = article.getArticleNote();
                articleNoteService.createOrUpdate(articleNote, persistArticle);
                mailService.sendSecretaryVerificationmail(persistArticle, articleNote);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }

    @Override
    public List<Article> findByAuthorId(Long authorId) {
        return articleRepositoryJPA.findByAuthorId(authorId);
    }

    // @Override
    // public Article endInvitationReview(Long articleId) throws Exception {
    // Article persistArticle = retrieve(articleId);
    // if
    // (persistArticle.getStatus().equals(ArticleStatus.INVITING_REVIEWER.toString()))
    // {
    // Integer acceptedReviewTotal =
    // reviewArticleRepositoryJPA.countAcceptedReview(articleId);
    // if (acceptedReviewTotal > 0) {
    // persistArticle.setStatus(ArticleStatus.IN_REVIEW.toString());
    // articleDateService.addOrUpdate(persistArticle,
    // DateTypeName.IN_REVIEW_DATE.toString());
    // return articleRepositoryJPA.save(persistArticle);
    // } else {
    // throw new Exception("Bài báo này chưa có reviewer nào!");
    // }
    // } else {
    // throw new Exception("Thất bại! Đã có sự cập nhật trạng thái cho bài đăng này!
    // Vui lòng quay về danh sách bài đăng!");
    // }
    // }

    @Override
    public Article editorDecide(Long articleId, String status, ArticleNote articleNote) throws Exception {
        Article article = retrieve(articleId);
        if (article.getStatus().equals(ArticleStatus.IN_REVIEW.toString())) {
            if (status.equals(ArticleStatus.ACCEPT.toString()) || status.equals(ArticleStatus.REJECT.toString())) {
                article.setStatus(status);
                articleDateService.addOrUpdate(article, DateTypeName.DECIDED_DATE.toString());
                articleRepositoryJPA.save(article);
                articleNoteService.createOrUpdate(articleNote, article);
                return article;
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
            Optional<AuthorArticle> authorArticleOptional = authorArticleRepositoryJPA.findByArticleAndUser(articleId,
                    userId);
            if (authorArticleOptional.isPresent()) {
                AuthorArticle authorArticle = authorArticleOptional.get();
                if (authorRoleRepositoryJPA.findByAuthorArticleAndAuthorType(authorArticle.getId(),
                        AuthorType.CORRESPONDING_AUTHOR.toString()).isPresent()) {
                    Article article = retrieve(articleId);
                    article.setStatus(ArticleStatus.WITHDRAW.toString());
                    articleDateService.addOrUpdate(article, DateTypeName.WITHDRAW_DATE.toString());
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

    @Override
    public List<Article> list(String status, Long editorId) {
        List<Article> articles = articleRepositoryJPA.list(status, editorId);
        articles.forEach(article -> {
            Manuscript currentManuscript = manuscriptService.getLastestManuscript(article.getId());
            if (status.equals(ArticleStatus.INVITING_REVIEWER.toString())) {
                Integer invitedReviewer = reviewArticleRepositoryJPA.countReviewArticle(currentManuscript.getId());
                article.setInvitedReviewer(invitedReviewer);
                if (invitedReviewer > 0) {
                    article.setWaitingResponseReviewer(reviewArticleRepositoryJPA.countReviewArticleByStatus(
                            currentManuscript.getId(), ReviewArticleStatus.PENDING.toString()));
                    article.setRejectedReviewer(reviewArticleRepositoryJPA.countReviewArticleByStatus(
                            currentManuscript.getId(), ReviewArticleStatus.REJECTED.toString()));
                }
            }
            if (status.equals(ArticleStatus.IN_REVIEW.toString())) {
                article.setWaitingResponseReviewer(reviewArticleRepositoryJPA.countReviewArticleByStatus(
                        currentManuscript.getId(), ReviewArticleStatus.PENDING.toString()));
                article.setAcceptedReviewer(reviewArticleRepositoryJPA.countReviewArticleByStatus(
                        currentManuscript.getId(), ReviewArticleStatus.ACCEPTED.toString()));
                article.setReviewedReviewer(reviewArticleRepositoryJPA.countReviewArticleByStatus(
                        currentManuscript.getId(), ReviewArticleStatus.REVIEWED.toString()));
            }
        });
        return articles;
    }

    @Override
    public Article retrieve(Long articleId, Long userId) throws Exception {
        Article article = retrieve(articleId);
        if (!article.getEditorUser().getId().equals(userId)) {
            throw new Exception("Bạn không có quyền để xem bài báo này!");
        } else {
            article.setReviewedReviewer(reviewArticleRepositoryJPA.countReviewArticleByStatus(
                    manuscriptService.getLastestManuscript(article.getId()).getId(),
                    ReviewArticleStatus.REVIEWED.toString()));
            return article;
        }
    }

    @Override
    public synchronized Article assignEditor(Long articleId, Long userId) throws Exception {
        User user = userService.retrieve(userId);
        return assignEditor(articleId, user);
    }

    @Override
    public synchronized Article assignEditor(Long articleId, String email) throws Exception {
        User user = userService.findByEmail(email);
        return assignEditor(articleId, user);
    }

    @Override
    public synchronized Article assignEditor(Long articleId, User user) throws Exception {
        Article article = retrieve(articleId);
        if (!article.getStatus().equals(ArticleStatus.ASSIGN_EDITOR.toString()) || article.getEditorUser() != null) {
            throw new Exception("Tình trạng bài viết không hợp lệ / Bài viết đã có biên tập viên");
        }

        article.setEditorUser(user);
        article.setStatus(ArticleStatus.INVITING_REVIEWER.toString());
        try {
            articleDateService.addOrUpdate(article, DateTypeName.ASSIGNED_EDITOR_DATE.toString());
            articleRepositoryJPA.save(article);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return article;
    }

    @Override
    public List<Article> getArticleWaitingForInviteReviewer(Long editorId) {
        List<Article> articles = list(ArticleStatus.INVITING_REVIEWER.toString(), editorId);
        return articles.stream().filter(article -> reviewArticleRepositoryJPA.countReviewArticle(
                manuscriptService.getLastestManuscript(article.getId()).getId()) == 0).collect(Collectors.toList());
    }

    @Override
    public List<Article> getArticleWaitingForAcceptFromReviewer(Long editorId) {
        List<Article> articles = list(ArticleStatus.INVITING_REVIEWER.toString(), editorId);
        return articles.stream().filter(article -> reviewArticleRepositoryJPA.countReviewArticle(
                manuscriptService.getLastestManuscript(article.getId()).getId()) > 0).collect(Collectors.toList());
    }

    @Override
    public List<Article> getInReviewArticles(Long editorId) {
        List<Article> articles = list(ArticleStatus.IN_REVIEW.toString(), editorId);
        return articles.stream().filter(article -> reviewArticleRepositoryJPA.countReviewArticleByStatus(
                manuscriptService.getLastestManuscript(article.getId()).getId(),
                ReviewArticleStatus.REVIEWED.toString()) == 0).collect(Collectors.toList());
    }

    @Override
    public List<Article> getReviewedArticles(Long editorId) {
        List<Article> articles = list(ArticleStatus.IN_REVIEW.toString(), editorId);
        return articles.stream().filter(article -> reviewArticleRepositoryJPA.countReviewArticleByStatus(
                manuscriptService.getLastestManuscript(article.getId()).getId(),
                ReviewArticleStatus.REVIEWED.toString()) > 0).collect(Collectors.toList());
    }

    @Override
    public String getArticleStatusById(Long articleId) {
        return articleRepositoryJPA.getArticleStatus(articleId);
    }

    @Override
    public Long countArticleWaitingAssignEditor() {
        return articleRepositoryJPA.countArticleWaitingAssignEditor(ArticleStatus.ASSIGN_EDITOR.toString());
    }

    @Override
    public List<Article> findByEditorUserId(Long editorId) {
        return articleRepositoryJPA.findByEditorUserId(editorId);
    }

    @Override
    public Long countAssignedArticleById(Long editorId) {
        return articleRepositoryJPA.countAssignedArticleById(editorId);
    }
}
