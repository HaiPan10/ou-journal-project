package com.ou.journal.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.http.client.RedirectException;
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

    @Override
    public ReviewArticle create(User user, Article article) {
        if (article.getStatus().equals(ArticleStatus.IN_REVIEW.toString())) {
            if (user.getId() == null) {
                userService.create(user);
            } else if (reviewArticleRepositoryJPA.findByUserAndArticle(user, article).isPresent()) {
                return null;
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
        // Optional<ReviewArticle> reviewArticleOptional =
        // reviewArticleRepositoryJPA.findById(reviewArticleId);
        // if (reviewArticleOptional.isPresent()) {
        // ReviewArticle reviewArticle = reviewArticleOptional.get();
        // if
        // (reviewArticle.getArticle().getStatus().equals(ArticleStatus.IN_REVIEW.toString())
        // && reviewArticle.getStatus().equals(ReviewArticleStatus.PENDING.toString()))
        // {
        // if (reviewArticle.getUser().getEmail().equals(email) &&
        // reviewArticle.getUser().getId().equals(userId)) {
        // if (status.equals(ReviewArticleStatus.ACCEPTED.toString())) {
        // Optional<Account> accountOptional = accountService.getByEmail(email);
        // if (!accountOptional.isPresent()) {
        // // Account account = new Account();
        // // account.setUser(reviewArticle.getUser());
        // // account.setPassword(passwordEncoder.encode("123456"));
        // // account.setCreatedAt(new Date());
        // // account.setUpdatedAt(new Date());
        // // account.setStatus(AccountStatus.ACCEPTED.toString());
        // // account.setEmail(email);
        // // accountRepositoryJPA.save(account);
        // throw new AccountNotFoundException();
        // }
        // if (!userRoleRepositoryJPA.findByUserAndRoleName(reviewArticle.getUser(),
        // RoleName.ROLE_REVIEWER.toString()).isPresent()) {
        // UserRole userRole = new UserRole(reviewArticle.getUser(),
        // roleService.retrieve(RoleName.ROLE_REVIEWER.toString()));
        // userRoleRepositoryJPA.save(userRole);
        // }
        // }
        // reviewArticle.setStatus(status);
        // return reviewArticleRepositoryJPA.save(reviewArticle);
        // } else {
        // throw new Exception("Người gọi API không phải user được mời!");
        // }
        // } else {
        // throw new Exception("Lời mời không còn hợp lệ!");
        // }
        // } else {
        // throw new Exception("In valid review article");
        // }
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
            if (reviewArticle.getArticle().getStatus().equals(ArticleStatus.IN_REVIEW.toString())
                    && reviewArticle.getStatus().equals(ReviewArticleStatus.PENDING.toString())) {
                if (reviewArticle.getUser().getEmail().equals(email)
                        && reviewArticle.getUser().getId().equals(userId)) {
                    return true;
                } else {
                    throw new Exception("Người gọi API không phải user được mời!");
                }
            } else {
                throw new Exception("Lời mời không còn hợp lệ!");
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

}
