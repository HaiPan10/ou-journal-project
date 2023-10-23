package com.ou.journal.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ReviewArticle;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.ReviewArticleService;
import com.ou.journal.service.interfaces.UserService;
import com.ou.journal.validator.WebAppValidator;

@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private WebAppValidator webAppValidator;

    @Autowired
    private ReviewArticleService reviewArticleService;

    @GetMapping("/admin/articles")
    public String list(Model model, @RequestParam(name="status", required = false, defaultValue = "PENDING") String status) {
        List<Article> articles = new ArrayList<>();
        model.addAttribute("status", status);
        if (status.equals(ArticleStatus.PENDING.toString()) || status.equals(ArticleStatus.INVITING_REVIEWER.toString())) {
            articles = articleService.list(status);
        }
        model.addAttribute("articles", articles);
        return "articleManager";
    }

    @GetMapping("/admin/articles/{articleId}")
    public String retrieve(Model model, @PathVariable Long articleId) throws Exception {
        try {
            Article article = articleService.retrieve(articleId);
            if (article.getStatus().equals(ArticleStatus.PENDING.toString())) {
                model.addAttribute("viewUrl", String.format("/admin/articles/view/%s", article.getId()));
                model.addAttribute("article", article);
            } else {
                model.addAttribute("error", "invalid status");
            }

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "articleDetail";
    }
    
    @GetMapping("/admin/articles/accept/{articleId}")
    public String secretaryAccept(Model model, @PathVariable Long articleId) throws Exception {
        try {
            Article article = articleService.retrieve(articleId);
            model.addAttribute("viewUrl", String.format("/admin/articles/view/%s", article.getId()));
            model.addAttribute("article", article);

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "articleDetail";
    }

    @GetMapping("/admin/review-articles/{articleId}")
    public String viewReviewer(Model model, @PathVariable Long articleId) throws Exception {
        try {
            Article article = articleService.retrieve(articleId);
            if (article.getStatus().equals(ArticleStatus.INVITING_REVIEWER.toString())) {
                List<ReviewArticle> reviewArticles = reviewArticleService.findByArticle(article);
                model.addAttribute("reviewArticles", reviewArticles);
                model.addAttribute("articleId", articleId);
                model.addAttribute("article", article);
                List<Object[]> users = userService.listUser();
                model.addAttribute("users", users);
                User user = new User();
                model.addAttribute("user", user);
            } else {
                model.addAttribute("error", "in valid status");
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "articleReviewerManager";
    }

    @PostMapping(path = "/admin/review-articles/invite/{articleId}")
    public String inviteReviewer(@ModelAttribute("user") User user, @PathVariable Long articleId,
     Model model, BindingResult bindingResult) {
        try {
            System.out.println("CATCH");
            webAppValidator.validate(user, bindingResult);
            if (bindingResult.hasErrors()) {
                return "articleReviewerManager";
            }
            Article article = articleService.retrieve(articleId);
            reviewArticleService.create(user, article);
            return "redirect:/admin/review-articles/{articleId}";
        } catch (Exception e) {
            return "redirect:/admin/review-articles/{articleId}";
        }
    }
}
