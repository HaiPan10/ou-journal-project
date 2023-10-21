package com.ou.journal.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.MailService;
import com.ou.journal.service.interfaces.UserService;
import com.ou.journal.utils.FileConverterUtils;
import com.ou.journal.validator.WebAppValidator;

import jakarta.validation.Valid;

@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private WebAppValidator webAppValidator;

    @GetMapping("/admin/articles")
    public String list(Model model, @RequestParam(name="status", required = false, defaultValue = "PENDING") String status) {
        List<Article> articles = new ArrayList<>();
        if (status.equals(ArticleStatus.PENDING.toString()) || status.equals(ArticleStatus.IN_REVIEW.toString())) {
            articles = articleService.list(status);
        }
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

    // @GetMapping("/admin/articles/view/{articleId}")
    // public ResponseEntity<byte[]> view(@PathVariable Long articleId) throws Exception {
    //     Article article = articleService.retrieve(Long.valueOf(articleId));
    //     byte[] documentData = article.getCurrentManuscript().getContent();
    //     HttpHeaders headers = new HttpHeaders();
    //     byte[] htmlData;
    //     if (article.getCurrentManuscript().getType().equals("application/pdf")) {
    //         htmlData = FileConverterUtils.generateHTMLFromPDF(documentData);
    //     } else {
    //         byte[] pdfBytes = FileConverterUtils.convertToPDF(documentData);
    //         htmlData = FileConverterUtils.generateHTMLFromPDF(pdfBytes);
    //     }
    //     headers.setContentType(MediaType.TEXT_HTML);
    //     return new ResponseEntity<>(htmlData, headers, HttpStatus.OK);
    // }

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
            model.addAttribute("articleId", articleId);
            model.addAttribute("article", article);
            List<Object[]> users = userService.listUser();
            model.addAttribute("users", users);
            User user = new User();
            model.addAttribute("user", user);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "articleReviewerManager";
    }

    @PostMapping(path = "/admin/review-articles/invite/{articleId}")
    public String inviteReviewer(@ModelAttribute("user") User user, @PathVariable Long articleId,
     Model model, BindingResult bindingResult) {
        try {
            webAppValidator.validate(user, bindingResult);
            if (bindingResult.hasErrors()) {
                return "articleReviewerManager";
            }

            System.out.println("Email: " + user.getEmail());
            System.out.println("Lastname: " + user.getLastName());
            System.out.println("Firstname: " + user.getFirstName());
            return "redirect:/admin/review-articles/{articleId}";
        } catch (Exception e) {
            bindingResult.addError(new ObjectError("exceptionError", e.getMessage()));
            return "articleReviewerManager";
        }
    }
}
