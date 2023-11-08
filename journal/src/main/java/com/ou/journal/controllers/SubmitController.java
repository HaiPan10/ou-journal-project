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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.pojo.AuthorType;

import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.AuthorArticle;
import com.ou.journal.pojo.AuthorNote;
import com.ou.journal.pojo.AuthorRole;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.AccountService;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.UserService;

// test
@Controller
@SessionAttributes({ "article", "termsAccepted" })
public class SubmitController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @ModelAttribute("authorTypes")
    public com.ou.journal.enums.AuthorType[] getTypes() {
        return com.ou.journal.enums.AuthorType.values();
    }

    @ModelAttribute("termsAccepted")
    public Boolean setTermsAccepted() {
        return false;
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

                authorRoles.add(authorRole2);
                authorRoles.add(authorRole1);

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

                // Hai: add new author note
                AuthorNote authorNote = new AuthorNote();
                article.setAuthorNote(authorNote);

                return article;
            } catch (Exception e) {

            }
        }
        return null;
    }

    @GetMapping({ "/submit", "/submit/step1" })
    public String getSubmitPage1(@ModelAttribute("termsAccepted") Boolean termsAccepted, Model model) {
        model.addAttribute("termsAccepted", termsAccepted);
        return "client/submitManuscript/step1";
    }

    @GetMapping({ "/submit/step2" })
    public String getSubmitPage2(@ModelAttribute("article") Article article,
            @ModelAttribute("termsAccepted") Boolean termsAccepted) {
        if (termsAccepted == false)
            return "redirect:/submit/step1";
        return "client/submitManuscript/step2";
    }

    @GetMapping({ "/submit/step3" })
    public String getSubmitPage3(@ModelAttribute("article") Article article,
            @ModelAttribute("termsAccepted") Boolean termsAccepted, Model model) {
        if (termsAccepted == false)
            return "redirect:/submit/step1";
        if (article.getAbstracts() == null || article.getAbstracts().isBlank() || article.getTitle() == null
                || article.getTitle().isBlank()) {
            return "redirect:/submit/step2";
        }
        List<Object[]> users = userService.listUser();
        model.addAttribute("users", users);
        return "client/submitManuscript/step3";
    }

    @GetMapping({ "/submit/step4" })
    public String getSubmitPage4(@ModelAttribute("article") Article article,
            @ModelAttribute("termsAccepted") Boolean termsAccepted, Model model) {
        if (termsAccepted == false)
            return "redirect:/submit/step1";
        if (article.getAbstracts() == null || article.getAbstracts().isBlank() || article.getTitle() == null
                || article.getTitle().isBlank()) {
            return "redirect:/submit/step2";
        } else {
            final boolean[] isHasFirstAuthor = { false };
            final boolean[] isHasCorresponding = { false };
            article.getAuthorArticles().forEach(authorArticle -> {
                authorArticle.getAuthorRoles().forEach(role -> {
                    if (role.getAuthorType().getId() == 1)
                        isHasFirstAuthor[0] = true;
                    else if (role.getAuthorType().getId() == 2)
                        isHasCorresponding[0] = true;
                });
            });
            if (!isHasFirstAuthor[0] || !isHasCorresponding[0])
                return "redirect:/submit/step3";
        }
        if (article.getFile() != null) {
            String originalFilename = article.getFile().getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            model.addAttribute("extension", extension);
            model.addAttribute("fileName", originalFilename);
        }
        return "client/submitManuscript/step4";
    }

    @PostMapping({ "/submit/step1" })
    public String submitPage2(Model model) {
        model.addAttribute("termsAccepted", true);
        return "redirect:/submit/step2";
    }

    @PostMapping({ "/submit/step2" })
    public String submitPage2(@ModelAttribute("article") Article article, @RequestParam(name = "back", required = false) String back) {
        if (back != null)
            return "redirect:/submit/step1";
        return "redirect:/submit/step3";
    }

    @PostMapping({ "/submit/step3" })
    public String submitPage3(
            @ModelAttribute("article") Article article,
            @RequestParam(name = "back", required = false) String back,
            @RequestBody List<AuthorArticle> authorArticles) {
        article.setAuthorArticles(authorArticles);
        if (back != null)
            return "redirect:/submit/step2";
        return "redirect:/submit/step4";
    }

    @PostMapping({ "/submit/step4" })
    public String submitPage4(@RequestParam(name = "back", required = false) String back,
            @RequestParam("file") MultipartFile file, @ModelAttribute("article") Article article,
            SessionStatus sessionStatus) {
        if (file != null && !file.isEmpty())
            article.setFile(file);
        if (back != null)
            return "redirect:/submit/step3";

        try {
            articleService.create(article, article.getFile());
            sessionStatus.setComplete();

            return "redirect:/";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/submit/step4";
    }
}