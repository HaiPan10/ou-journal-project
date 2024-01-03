package com.ou.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.enums.ReviewArticleStatus;
import com.ou.journal.enums.RoleName;
import com.ou.journal.pojo.AuthenticationUser;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.ReviewArticleService;

@Controller
public class MainMenuController {
    // @Autowired
    // private UserRoleService userRoleService;

    // @Autowired
    // private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ReviewArticleService reviewArticleService;

    @GetMapping("main-menu")
    public String mainMenu(@AuthenticationPrincipal AuthenticationUser currentUser, Model model) throws Exception {
        String currentRole = currentUser.getRoleName();

        if (currentRole.equals(RoleName.ROLE_EDITOR.toString())) {
            model.addAttribute("articleWaitingForInviteReviewCount",
                    articleService.getArticleWaitingForInviteReviewer(currentUser.getId()).size());
            model.addAttribute("articleWaitingForAcceptFromReviewerCount",
                    articleService.getArticleWaitingForAcceptFromReviewer(currentUser.getId()).size());
            model.addAttribute("articleInReviewingCount",
                    articleService.getInReviewArticles(currentUser.getId()).size());
            model.addAttribute("articleReviewedCount",
                    articleService.getReviewedArticles(currentUser.getId()).size());
            model.addAttribute("articleWaitingAssignEditor",
                    articleService.countArticleWaitingAssignEditor());
            model.addAttribute("assignedArticleCount",
                    articleService.countAssignedArticleById(currentUser.getId()));
            model.addAttribute("pendingArticleCount",
                    articleService.countArticleByStatus(ArticleStatus.PENDING.toString()));

        } else if (currentRole.equals(RoleName.ROLE_REVIEWER.toString())) {
            model.addAttribute("invitationCount",
                    reviewArticleService.countReviewArticles(currentUser.getId(),
                            ReviewArticleStatus.PENDING.toString()));
            model.addAttribute("reviewCount",
                    reviewArticleService.countReviewArticles(currentUser.getId(),
                            ReviewArticleStatus.ACCEPTED.toString()));

        } else if (currentRole.equals(RoleName.ROLE_AUTHOR.toString())) {
            model.addAttribute("processingArticleCount",
                    articleService.countProcessingArticleByAuthorId(currentUser.getId()));

        } else if (currentRole.equals(RoleName.ROLE_SECRETARY.toString())) {
            model.addAttribute("pendingArticleCount",
                    articleService.countArticleByStatus(ArticleStatus.PENDING.toString()));

        }
        return "client/mainMenu";
    }
}
