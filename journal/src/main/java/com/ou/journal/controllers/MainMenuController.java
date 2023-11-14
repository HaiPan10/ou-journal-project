package com.ou.journal.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ou.journal.enums.ReviewArticleStatus;
import com.ou.journal.enums.RoleName;
import com.ou.journal.pojo.AuthenticationUser;
import com.ou.journal.pojo.UserRole;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.ReviewArticleService;
import com.ou.journal.service.interfaces.UserRoleService;
import com.ou.journal.service.interfaces.UserService;

@Controller
public class MainMenuController {
    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ReviewArticleService reviewArticleService;

    @GetMapping("main-menu")
    public String mainMenu(@AuthenticationPrincipal AuthenticationUser currentUser, Model model) throws Exception {
        Optional<UserRole> editorUserOptional = userRoleService.getByUserAndRoleName(
                userService.retrieve(currentUser.getId()), RoleName.ROLE_EDITOR.toString());
        if (editorUserOptional.isPresent()) {
            model.addAttribute("articleWaitingForInviteReviewCount",
                    articleService.getArticleWaitingForInviteReviewer(currentUser.getId()).size());
            model.addAttribute("articleWaitingForAcceptFromReviewerCount",
                    articleService.getArticleWaitingForAcceptFromReviewer(currentUser.getId())
                            .size());
            model.addAttribute("articleInReviewingCount",
                    articleService.getInReviewArticles(currentUser.getId()).size());
            model.addAttribute("articleReviewedCount",
                    articleService.getReviewedArticles(currentUser.getId()).size());
            model.addAttribute("articleWaitingAssignEditor",
                    articleService.countArticleWaitingAssignEditor());
            model.addAttribute("assignedArticleCount",
                    articleService.countAssignedArticleById(currentUser.getId()));
        }

        Optional<UserRole> reviewerUserOptional = userRoleService.getByUserAndRoleName(
                userService.retrieve(currentUser.getId()), RoleName.ROLE_REVIEWER.toString());
        if (reviewerUserOptional.isPresent()) {
            model.addAttribute("invitationCount",
                    reviewArticleService.countReviewArticles(currentUser.getId(),
                            ReviewArticleStatus.PENDING.toString()));
            model.addAttribute("reviewCount",
                    reviewArticleService.countReviewArticles(currentUser.getId(),
                            ReviewArticleStatus.ACCEPTED.toString()));
        }

        Optional<UserRole> authorUserOptional = userRoleService.getByUserAndRoleName(
                userService.retrieve(currentUser.getId()), RoleName.ROLE_AUTHOR.toString());
        if (authorUserOptional.isPresent()) {
            model.addAttribute("processingArticleCount",
                    articleService.countProcessingArticleByAuthorId(currentUser.getId()));
        }
        return "client/mainMenu";
    }
}
