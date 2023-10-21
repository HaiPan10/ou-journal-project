package com.ou.journal.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.pojo.AuthorType;

import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.AuthorArticle;
import com.ou.journal.pojo.AuthorRole;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.AccountService;

// test
@Controller
@SessionAttributes("article")
public class SubmitController {
    @Autowired
    private AccountService accountService;

    @ModelAttribute("authorTypes")
    public com.ou.journal.enums.AuthorType[] getTypes() {
        return com.ou.journal.enums.AuthorType.values();
    }

    @ModelAttribute("article")
    public Article setUpArticle() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            try {

                List<AuthorRole> authorRoles = new ArrayList<>();
                AuthorRole authorRole1 = new AuthorRole();
                AuthorType type1 = new AuthorType();
                type1.setId(Long.valueOf(1));
                authorRole1.setAuthorType(type1);

                AuthorRole authorRole2 = new AuthorRole();
                AuthorType type2 = new AuthorType();
                type2.setId(Long.valueOf(2));
                authorRole2.setAuthorType(type2);

                authorRoles.add(authorRole1);
                authorRoles.add(authorRole2);

                List<AuthorArticle> authorArticles = new ArrayList<>();
                AuthorArticle authorArticle = new AuthorArticle();
                authorArticle.setAuthorRoles(authorRoles);

                Account account = accountService.findByUserName(username);
                User user = new User();
                user.setId(account.getUser().getId());
                user.setFirstName(account.getUser().getFirstName());
                user.setLastName(account.getUser().getLastName());
                user.setEmail(account.getUser().getEmail());

                authorArticle.setUser(user);

                authorArticles.add(authorArticle);
                Article article = new Article();
                article.setAuthorArticles(authorArticles);

                return article;
            } catch (Exception e) {

            }
        }
        return null;
    }

    @GetMapping({ "/submit", "/submit/step1" })
    public String getSubmitPage1(@ModelAttribute("article") Article article) {
        return "client/submitManuscript/step1";
    }

    @GetMapping({ "/submit/step2" })
    public String getSubmitPage2(@ModelAttribute("article") Article article) {
        return "client/submitManuscript/step2";
    }

    @GetMapping({ "/submit/step3" })
    public String getSubmitPage3(@ModelAttribute("article") Article article, Model model) {
        if (article.getFile() != null) {
            String originalFilename = article.getFile().getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            model.addAttribute("extension", extension);
            model.addAttribute("fileName", originalFilename);
        }
        return "client/submitManuscript/step3";
    }

    @PostMapping({ "/submit/step1" })
    public String submitPage1(@ModelAttribute("article") Article article) {
        return "redirect:/submit/step2";
    }

    @PostMapping({ "/submit/step2" })
    public String submitPage2(
            @ModelAttribute("article") Article article,
            @RequestParam(name = "back", required = false) String back,
            @RequestBody List<AuthorArticle> authorArticles) {
        article.setAuthorArticles(authorArticles);
        if (back != null)
            return "redirect:/submit/step1";
        return "redirect:/submit/step3";
    }

    @PostMapping({ "/submit/step3" })
    public String submitPage3(@RequestParam(name = "back", required = false) String back,
            @RequestParam("file") MultipartFile file, @ModelAttribute("article") Article article) {
        if (file != null && !file.isEmpty())
            article.setFile(file);
        if (back != null)
            return "redirect:/submit/step2";
        return "redirect:/submit/step3";
    }
}