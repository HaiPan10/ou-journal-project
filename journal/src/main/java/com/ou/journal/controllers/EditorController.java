package com.ou.journal.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.pojo.Article;
import com.ou.journal.service.interfaces.ArticleService;

@Controller
public class EditorController {
    @Autowired
    private ArticleService articleService;

    @GetMapping("/editor/deciding-list")
    public String getDecidingList(Model model) {        
        try {
            List<Article> articles = articleService.list(ArticleStatus.DECIDING.toString());
            model.addAttribute("articles", articles);
        } catch (Exception e) {
            model.addAttribute("articles", new ArrayList<Article>());
        }
        
        return "client/decidingList";
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
        
        return "client/decideArticle";
    }
}
