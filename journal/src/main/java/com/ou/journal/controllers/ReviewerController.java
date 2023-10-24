package com.ou.journal.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.ou.journal.components.UserSessionInfo;
import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.enums.ReviewArticleStatus;
import com.ou.journal.pojo.ReviewArticle;
import com.ou.journal.service.interfaces.ReviewArticleService;

@Controller
public class ReviewerController {
    @Autowired
    private ReviewArticleService reviewArticleService;

    @Autowired
    private UserSessionInfo userSessionInfo;

    // @Autowired
    // private ArticleService articleService;

    @GetMapping("/reviewer/invitation-list")
    public String getInvitationList(Model model) {        
        try {
            List<ReviewArticle> reviewArticles = reviewArticleService.getReviewArticles(
                userSessionInfo.getCurrentAccount().getId(), 
                ReviewArticleStatus.PENDING.toString(), 
                ArticleStatus.INVITING_REVIEWER.toString());
            model.addAttribute("reviewArticles", reviewArticles);
        } catch (Exception e) {
            model.addAttribute("reviewArticles", new ArrayList<ReviewArticle>());
        }
        
        return "client/invitationList";
    }

    @GetMapping("/reviewer/review-list")
    public String getReviewArticles(Model model) {
        try {
            List<ReviewArticle> reviewArticles = reviewArticleService.getReviewArticles(
                userSessionInfo.getCurrentAccount().getId(), 
                ReviewArticleStatus.ACCEPTED.toString(), 
                ArticleStatus.IN_REVIEW.toString());
            model.addAttribute("reviewArticles", reviewArticles);
        } catch (Exception e) {
            model.addAttribute("reviewArticles", new ArrayList<ReviewArticle>());
        }
        return "client/inReviewArticle";
    }

    @GetMapping("/reviewer/review/{reviewArticleId}")
    public String reviewArticle(Model model, @PathVariable Long reviewArticleId){
        try {
            ReviewArticle reviewArticle = reviewArticleService.retrieve(reviewArticleId);
            if (!reviewArticle.getStatus().equals(ReviewArticleStatus.ACCEPTED.toString())) {
                return "redirect:/reviewer/review-list";
            }
            model.addAttribute("viewUrl", String.format("/api/articles/view/%s", reviewArticle.getArticle().getId()));
            model.addAttribute("reviewArticle", reviewArticle);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "client/reviewArticle";
    }
}
