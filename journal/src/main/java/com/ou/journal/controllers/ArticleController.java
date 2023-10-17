package com.ou.journal.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/admin/articles/{articleId}")
    public String retrieve(Model model, @PathVariable Long articleId) throws Exception {
        try {
            Article article = articleService.retrieve(articleId);
            model.addAttribute("viewUrl", String.format("/admin/articles/view/%s", article.getId()));
            model.addAttribute("article", article);

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "articleDetail";
    }

    @GetMapping("/admin/articles/view/{articleId}")
    public ResponseEntity<byte[]> download(@PathVariable Long articleId) throws Exception {
        Article article = articleService.retrieve(Long.valueOf(articleId));
        byte[] documentData = article.getCurrentManuscript().getContent();
        HttpHeaders headers = new HttpHeaders();
        if (article.getCurrentManuscript().getType().equals("application/pdf")) {
            headers.setContentType(MediaType.APPLICATION_PDF);
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
        headers.setContentLength(documentData.length);

        return new ResponseEntity<>(documentData, headers, HttpStatus.OK);
    }
}