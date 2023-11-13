package com.ou.journal.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.ou.journal.enums.ReviewArticleStatus;
import com.ou.journal.pojo.AuthenticationUser;
import com.ou.journal.pojo.Manuscript;
import com.ou.journal.pojo.ReviewArticle;
import com.ou.journal.service.interfaces.ManuscriptService;
import com.ou.journal.service.interfaces.ReviewArticleService;
import com.ou.journal.utils.EnumUtils;

@Controller
public class ReviewerController {
    @Autowired
    private ReviewArticleService reviewArticleService;

    @Autowired
    private ManuscriptService manuscriptService;

    @GetMapping("/reviewer/invitation-list")
    public String getInvitationList(Model model, @AuthenticationPrincipal AuthenticationUser currentUser) {
        try {
            List<ReviewArticle> reviewArticles = reviewArticleService.getReviewArticles(
                    currentUser.getId(),
                    ReviewArticleStatus.PENDING.toString());
            model.addAttribute("reviewArticles", reviewArticles);
        } catch (Exception e) {
            model.addAttribute("reviewArticles", new ArrayList<ReviewArticle>());
        }

        return "client/reviewer/invitationList";
    }

    @GetMapping("/reviewer/review-list")
    public String getReviewArticles(Model model, @AuthenticationPrincipal AuthenticationUser currentUser) {
        try {
            List<ReviewArticle> reviewArticles = reviewArticleService.getReviewArticles(
                    currentUser.getId(),
                    ReviewArticleStatus.ACCEPTED.toString());
            model.addAttribute("reviewArticles", reviewArticles);
        } catch (Exception e) {
            model.addAttribute("reviewArticles", new ArrayList<ReviewArticle>());
        }
        return "client/reviewer/inReviewArticle";
    }

    @GetMapping("/reviewer/review/{reviewArticleId}")
    public String reviewArticle(Model model, @PathVariable Long reviewArticleId,
            @AuthenticationPrincipal AuthenticationUser currentUser) {
        try {
            ReviewArticle reviewArticle = reviewArticleService.retrieve(reviewArticleId, currentUser.getId());
            if (!reviewArticle.getStatus().equals(ReviewArticleStatus.ACCEPTED.toString())) {
                return "redirect:/reviewer/review-list";
            }
            model.addAttribute("viewUrl",
                    String.format("/api/articles/view/%s", reviewArticle.getManuscript().getArticle().getId()));
            model.addAttribute("reviewArticle", reviewArticle);
            model.addAttribute("article", reviewArticle.getManuscript().getArticle());
            model.addAttribute("articleStatusEnum", EnumUtils.getArticleStatus());

            model.addAttribute("renderManuscript", reviewArticle.getManuscript());
            model.addAttribute("anonymousUrl", String.format("/api/articles/view-anonymous/%s",
                    reviewArticle.getManuscript().getArticle().getId()));
            model.addAttribute("appendixUrl", String.format("/api/articles/view-appendix/%s",
                    reviewArticle.getManuscript().getArticle().getId()));

            return "client//reviewer/reviewArticle";
        } catch (Exception e) {
            // model.addAttribute("error", e.getMessage());
            return "redirect:/reviewer/review-list";
        }

    }
}
