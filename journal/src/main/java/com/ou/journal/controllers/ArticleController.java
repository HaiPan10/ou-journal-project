package com.ou.journal.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.thymeleaf.context.Context;

import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.MailRequest;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.MailService;
import com.ou.journal.utils.FileConverterUtils;

@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private MailService mailService;

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
    public ResponseEntity<byte[]> view(@PathVariable Long articleId) throws Exception {
        Article article = articleService.retrieve(Long.valueOf(articleId));
        byte[] documentData = article.getCurrentManuscript().getContent();
        HttpHeaders headers = new HttpHeaders();
        byte[] htmlData;
        if (article.getCurrentManuscript().getType().equals("application/pdf")) {
            htmlData = FileConverterUtils.generateHTMLFromPDF(documentData);
        } else {
            byte[] pdfBytes = FileConverterUtils.convertToPDF(documentData);
            htmlData = FileConverterUtils.generateHTMLFromPDF(pdfBytes);
        }
        headers.setContentType(MediaType.TEXT_HTML);
        return new ResponseEntity<>(htmlData, headers, HttpStatus.OK);
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
}
