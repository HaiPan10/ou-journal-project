package com.ou.journal.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.ou.journal.components.UserSessionInfo;
import com.ou.journal.pojo.Article;
import com.ou.journal.service.interfaces.ArticleService;

@Controller
public class SubmissionController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserSessionInfo userSessionInfo;

    @ModelAttribute("articleStatusEnum")
    public com.ou.journal.enums.ArticleStatus[] getArticleStatus() {
        return com.ou.journal.enums.ArticleStatus.values();
    }

    @ModelAttribute("authorTypesEnum")
    public com.ou.journal.enums.AuthorType[] getTypes() {
        return com.ou.journal.enums.AuthorType.values();
    }
    
    @GetMapping("/submission/processing")
    public String processedSubmission(Model model){
        try {
            System.out.println("[DEBUG] - " + userSessionInfo.getCurrentAccount().getUserName());
            List<Article> articles = articleService.findByAuthorId(userSessionInfo.getCurrentAccount().getId());
            model.addAttribute("articles", articles);
            System.out.println("[DEBUG] - Finish Fetching");
        } catch (Exception e) {
            model.addAttribute("articles", new ArrayList<Article>());
            System.out.println("[DEBUG] - Catch exception: " + e.getMessage());
        }

        return "client/processingSubmission";
    }

    @GetMapping("/submission/processing/{articleId}")
    public String processedSubmissionDetail(Model model, @PathVariable Long articleId){
        try {
            Article article = articleService.retrieve(articleId);
            model.addAttribute("viewUrl", String.format("/api/articles/view/%s", article.getId()));
            model.addAttribute("article", article);

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "client/articleDetail";
    }
}
