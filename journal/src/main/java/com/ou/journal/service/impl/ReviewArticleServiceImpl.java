package com.ou.journal.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ou.journal.enums.AccountStatus;
import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.enums.ReviewArticleStatus;
import com.ou.journal.enums.RoleName;
import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ReviewArticle;
import com.ou.journal.pojo.User;
import com.ou.journal.pojo.UserRole;
import com.ou.journal.repository.AccountRepositoryJPA;
import com.ou.journal.repository.AuthorArticleRepositoryJPA;
import com.ou.journal.repository.ReviewArticleRepositoryJPA;
import com.ou.journal.repository.UserRoleRepositoryJPA;
import com.ou.journal.service.interfaces.ReviewArticleService;
import com.ou.journal.service.interfaces.RoleService;
import com.ou.journal.service.interfaces.UserService;
import com.ou.journal.service.interfaces.AccountService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReviewArticleServiceImpl implements ReviewArticleService {
    @Autowired
    private ReviewArticleRepositoryJPA reviewArticleRepositoryJPA;
    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepositoryJPA accountRepositoryJPA;
    @Autowired
    private UserRoleRepositoryJPA userRoleRepositoryJPA;
    @Autowired
    private RoleService roleService;
    @Autowired
    private AuthorArticleRepositoryJPA authorArticleRepositoryJPA;

    @Override
    public ReviewArticle create(User user, Article article) throws Exception {
        if (article.getStatus().equals(ArticleStatus.INVITING_REVIEWER.toString())) {
            if (user.getId() == null) {
                userService.create(user);
            } else if (user.getId() != null && reviewArticleRepositoryJPA.findByUserAndArticle(user, article).isPresent()) {
                throw new Exception("Reviewer này đã được mời!");
            } else if (user.getId() != null && authorArticleRepositoryJPA.findByArticleAndUser(article.getId(), user.getId()).isPresent()) {
                throw new Exception("Không thể mời tác giả review bài đăng của mình!");
            }
            ReviewArticle reviewArticle = new ReviewArticle();
            reviewArticle.setUser(user);
            reviewArticle.setArticle(article);
            reviewArticle.setInvitedAt(new Date());
            reviewArticle.setStatus(ReviewArticleStatus.PENDING.toString());
            return reviewArticleRepositoryJPA.save(reviewArticle);
        }
        return null;
    }

    @Override
    public List<ReviewArticle> findByArticle(Article article) {
        return reviewArticleRepositoryJPA.findByArticle(article);
    }

    @Override
    public ReviewArticle changeReviewStatus(Long reviewArticleId, String status,
            String email, Long userId) throws Exception {
        try {
            Optional<ReviewArticle> reviewArticleOptional = reviewArticleRepositoryJPA.findById(reviewArticleId);
            ReviewArticle reviewArticle = reviewArticleOptional.get();
            if (checkArticleAvailable(reviewArticleId, email, userId)) {                
                if (status.equals(ReviewArticleStatus.ACCEPTED.toString())) {
                    Optional<Account> accountOptional = accountService.getByEmail(email);
                    if (!accountOptional.isPresent()) {
                        throw new AccountNotFoundException();
                    }
                    if (!userRoleRepositoryJPA.findByUserAndRoleName(reviewArticle.getUser(),
                            RoleName.ROLE_REVIEWER.toString()).isPresent()) {
                        UserRole userRole = new UserRole(reviewArticle.getUser(),
                                roleService.retrieve(RoleName.ROLE_REVIEWER.toString()));
                        userRoleRepositoryJPA.save(userRole);
                    }
                }
                reviewArticle.setStatus(status);
                return reviewArticleRepositoryJPA.save(reviewArticle);
            } else {
                return reviewArticle;
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public boolean checkArticleAvailable(Long reviewArticleId, String email, Long userId) throws Exception {
        Optional<ReviewArticle> reviewArticleOptional = reviewArticleRepositoryJPA.findById(reviewArticleId);
        if (reviewArticleOptional.isPresent()) {
            ReviewArticle reviewArticle = reviewArticleOptional.get();
            if (reviewArticle.getArticle().getStatus().equals(ArticleStatus.INVITING_REVIEWER.toString())
                    && reviewArticle.getStatus().equals(ReviewArticleStatus.PENDING.toString())) {
                if (reviewArticle.getUser().getEmail().equals(email)
                        && reviewArticle.getUser().getId().equals(userId)) {
                    return true;
                } else {
                    throw new Exception("Người gọi API không phải user được mời!");
                }
            } else if (reviewArticle.getArticle().getStatus().equals(ArticleStatus.IN_REVIEW.toString())) {
                throw new Exception("Bài đăng này đã tiến hành review!");
            } else if (reviewArticle.getArticle().getStatus().equals(ArticleStatus.WITHDRAW.toString())) {
                throw new Exception("Bài đăng này đã bị rút!");
            } else {
                throw new Exception("Bài đăng này không còn mời review!");
            }
        } else {
            throw new Exception("Lời mời không hợp lệ!");
        }
    }

    @Override
    public ReviewArticle acceptReviewAndCreateAccount(Long reviewArticleId, String email, Long userId,
            Account account) throws Exception {
        try {
            Optional<ReviewArticle> reviewArticleOptional = reviewArticleRepositoryJPA.findById(reviewArticleId);
            ReviewArticle reviewArticle = reviewArticleOptional.get();
            if (accountRepositoryJPA.findByEmail(account.getEmail()).isPresent()) {
                throw new Exception(String.format("Email %s đã tồn tại!", account.getEmail()));
            }

            if (accountRepositoryJPA.findByUserName(account.getUserName()).isPresent()) {
                throw new Exception(String.format("Username %s đã tồn tại!", account.getUserName()));
            }
            account.setUser(reviewArticle.getUser());
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            account.setCreatedAt(new Date());
            account.setUpdatedAt(new Date());
            account.setStatus(AccountStatus.ACCEPTED.toString());
            account.setEmail(email);
            accountRepositoryJPA.save(account);
            return changeReviewStatus(reviewArticleId, ReviewArticleStatus.ACCEPTED.toString(), email, userId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public boolean checkResponseRevviewArticleInvitation(Long reviewArticleId, Long userId, String email) {
        Optional<ReviewArticle> reviewArticleOptional = reviewArticleRepositoryJPA.findById(reviewArticleId);
        if (reviewArticleOptional.isPresent()) {
            ReviewArticle reviewArticle = reviewArticleOptional.get();
            if (reviewArticle.getUser().getEmail().equals(email) &&
                    reviewArticle.getUser().getId().equals(userId) &&
                    !reviewArticle.getStatus().equals(ReviewArticleStatus.PENDING.toString())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public List<ReviewArticle> getReviewArticles(Long userId, String reviewArticleStatus, String articleStatus) {
        return reviewArticleRepositoryJPA.getReviewArticles(userId, reviewArticleStatus, articleStatus);
    }

    @Override
    public ReviewArticle retrieve(Long reviewArticle) throws Exception {
        Optional<ReviewArticle> reviewArticleOptional = reviewArticleRepositoryJPA.findById(reviewArticle);
        if (reviewArticleOptional.isPresent()) {
            return reviewArticleOptional.get();
        } else {
            throw new Exception("Lượt review này không tồn tại!");
        }
    }

    @Override
    public ReviewArticle doneReview(Long reviewArticleId, Long userId) throws Exception {
        ReviewArticle reviewArticle = retrieve(reviewArticleId);
        if (!reviewArticle.getUser().getId().equals(userId)) {
            throw new Exception("Bạn không có quyền review lượt review này!");
        }
        if (!reviewArticle.getStatus().equals(ReviewArticleStatus.ACCEPTED.toString())) {
            throw new Exception("Lượt review này có trạng thái không hợp lệ!");
        }
        reviewArticle.setStatus(ReviewArticleStatus.REVIEWED.toString());
        return reviewArticleRepositoryJPA.save(reviewArticle);
    }
}
