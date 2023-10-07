package com.ou.journal.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ou.journal.pojo.Article;
import com.ou.journal.service.interfaces.ArticleService;

@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @GetMapping("/admin/articles")
    public String list(Model model) {
        List<Article> articles = articleService.listPendingArticles();
        model.addAttribute("articles", articles);
        return "articleManager";
    }
}
