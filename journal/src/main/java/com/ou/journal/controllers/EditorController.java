package com.ou.journal.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ReviewArticle;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.ReviewArticleService;
import com.ou.journal.service.interfaces.UserService;
import com.ou.journal.validator.WebAppValidator;

@Controller
public class EditorController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ReviewArticleService reviewArticleService;
    @Autowired
    private UserService userService;
    @Autowired
    private WebAppValidator webAppValidator;

    @GetMapping("/editor/deciding-list")
    public String getDecidingList(Model model) {        
        try {
            List<Article> articles = articleService.list(ArticleStatus.DECIDING.toString());
            model.addAttribute("articles", articles);
        } catch (Exception e) {
            model.addAttribute("articles", new ArrayList<Article>());
        }
        
        return "client/editor/decidingList";
    }


    @GetMapping("/editor/review/{articleId}")
    public String getDecidingList(Model model, @PathVariable Long articleId) {        
        try {
            Article article = articleService.retrieve(articleId);
            if (!article.getStatus().equals(ArticleStatus.DECIDING.toString())) {
                return "redirect:/editor/deciding-list";
            }
            model.addAttribute("viewUrl", String.format("/api/articles/view/%s", article.getId()));
            model.addAttribute("article", article);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        
        return "client/editor/decideArticle";
    }


    @GetMapping("/editor/review-articles")
    public String getReviewList(Model model) {        
        List<Article> articles = new ArrayList<>();
        articles = articleService.list(ArticleStatus.INVITING_REVIEWER.toString());
        model.addAttribute("articles", articles);
        return "client/editor/invitingReviewList";
    }

    @GetMapping("/editor/review-articles/{articleId}")
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
        return "client/editor/articleReviewerManager";
    }

    @PostMapping(path = "/editor/review-articles/invite/{articleId}")
    public String inviteReviewer(@ModelAttribute("user") User user, @PathVariable Long articleId,
     Model model, BindingResult bindingResult) throws Exception {
        Article article = articleService.retrieve(articleId);
        List<ReviewArticle> reviewArticles = reviewArticleService.findByArticle(article);
        List<Object[]> users = userService.listUser();
        try {            
            webAppValidator.validate(user, bindingResult);
            if (bindingResult.hasErrors()) {
                model.addAttribute("article", article);
                model.addAttribute("reviewArticles", reviewArticles);
                model.addAttribute("articleId", articleId);
                model.addAttribute("users", users);
                return "client/editor/articleReviewerManager";
            }
            reviewArticleService.create(user, article);
            return "redirect:/admin/review-articles/{articleId}";
        } catch (Exception e) {
            model.addAttribute("article", article);
            model.addAttribute("reviewArticles", reviewArticles);
            model.addAttribute("articleId", articleId);
            model.addAttribute("users", users);
            bindingResult.addError(new ObjectError("exceptionError", e.getMessage()));
            return "client/editor/articleReviewerManager";
        }
    }
}
