package com.ou.journal.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ou.journal.pojo.AuthorType;
import com.ou.journal.pojo.Manuscript;
import com.ou.journal.components.CountryProperties;
import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.ArticleCategory;
import com.ou.journal.pojo.AuthorArticle;
import com.ou.journal.pojo.AuthorNote;
import com.ou.journal.pojo.AuthorRole;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.AccountService;
import com.ou.journal.service.interfaces.ArticleCategoryService;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.UserService;

// test
@Controller
@SessionAttributes({ "article", "termsAccepted" })
public class SubmitController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private ArticleCategoryService articleCategoryService;

    @Autowired
    private CountryProperties countryProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @ModelAttribute("authorTypes")
    public com.ou.journal.enums.AuthorType[] getTypes() {
        return com.ou.journal.enums.AuthorType.values();
    }

    @ModelAttribute("countries")
    public Map<String, String> getCountries() {
        return countryProperties.getCountries();
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
                authorArticle.setAuthorOrder(1);
                authorArticles.add(authorArticle);
                Article article = new Article();
                article.setAuthorArticles(authorArticles);

                // Hai: add new author note
                AuthorNote authorNote = new AuthorNote();
                article.setAuthorNote(authorNote);

                Manuscript manuscript = new Manuscript();
                article.setCurrentManuscript(manuscript);

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
    public String test(Model model) {
        List<ArticleCategory> cates = articleCategoryService.findAll();
        model.addAttribute("cates", cates);
        return "client/submitManuscript/step2";
    }

    @GetMapping({ "/submit/step3" })
    public String getSubmitPage3(@ModelAttribute("article") Article article,
            @ModelAttribute("termsAccepted") Boolean termsAccepted) {
        if (termsAccepted == false)
            return "redirect:/submit/step1";
        if (article.getArticleCategory() == null)
            return "redirect:/submit/step2";
        return "client/submitManuscript/step3";
    }

    @GetMapping({ "/submit/step4" })
    public String getSubmitPage4(@ModelAttribute("article") Article article,
            @ModelAttribute("termsAccepted") Boolean termsAccepted, Model model) {
        if (termsAccepted == false)
            return "redirect:/submit/step1";
        if (article.getArticleCategory() == null)
            return "redirect:/submit/step2";
        if (article.getAbstracts() == null || article.getAbstracts().isBlank() || article.getTitle() == null
                || article.getTitle().isBlank()) {
            return "redirect:/submit/step3";
        }
        List<Object[]> users = userService.listUser();
        model.addAttribute("users", users);
        return "client/submitManuscript/step4";
    }

    @GetMapping({ "/submit/step5" })
    public String getSubmitPage5(@ModelAttribute("article") Article article,
            @ModelAttribute("termsAccepted") Boolean termsAccepted, Model model) {
        if (termsAccepted == false)
            return "redirect:/submit/step1";
        if (article.getArticleCategory() == null)
            return "redirect:/submit/step2";
        if (article.getAbstracts() == null || article.getAbstracts().isBlank() || article.getTitle() == null
                || article.getTitle().isBlank()) {
            return "redirect:/submit/step3";
        }
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
            return "redirect:/submit/step4";
        return "client/submitManuscript/step5";
    }

    @GetMapping({ "/submit/step6" })
    public String getSubmitPage6(@ModelAttribute("article") Article article,
            @ModelAttribute("termsAccepted") Boolean termsAccepted, Model model) {
        if (termsAccepted == false)
            return "redirect:/submit/step1";
        if (article.getArticleCategory() == null)
            return "redirect:/submit/step2";
        if (article.getAbstracts() == null || article.getAbstracts().isBlank() || article.getTitle() == null
                || article.getTitle().isBlank()) {
            return "redirect:/submit/step3";
        }
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
            return "redirect:/submit/step4";

        String reference = article.getCurrentManuscript().getReference().trim();

        if (reference == null || reference.isEmpty())
            return "redirect:/submit/step5";
        article.getCurrentManuscript().setReference(reference);
        return "client/submitManuscript/step6";
    }

    @PostMapping({ "/submit/step1" })
    public String submitPage1(Model model) {
        model.addAttribute("termsAccepted", true);
        return "redirect:/submit/step2";
    }

    @PostMapping({ "/submit/step2" })
    public String submitPage2(@ModelAttribute("article") Article article,
            @RequestParam(name = "back", required = false) String back) {
        if (back != null)
            return "redirect:/submit/step1";
        return "redirect:/submit/step3";
    }

    @PostMapping({ "/submit/step3" })
    public String submitPage3(@ModelAttribute("article") Article article,
            @RequestParam(name = "back", required = false) String back) {
        if (back != null)
            return "redirect:/submit/step2";
        return "redirect:/submit/step4";
    }

    @PostMapping({ "/submit/step4" })
    public String submitPage4(
            @ModelAttribute("article") Article article,
            @RequestParam(name = "back", required = false) String back,
            @RequestBody List<AuthorArticle> authorArticles) {
        article.setAuthorArticles(authorArticles);
        if (back != null)
            return "redirect:/submit/step3";
        return "redirect:/submit/step5";
    }

    @PostMapping({ "/submit/step5" })
    public String submitPage5(@ModelAttribute("article") Article article,
            @RequestParam(name = "back", required = false) String back) {
        if (back != null)
            return "redirect:/submit/step4";
        return "redirect:/submit/step6";
    }

    @PostMapping({ "/submit/step6" })
    public String submitPage6(@RequestParam(name = "back", required = false) String back,
            @ModelAttribute("article") Article article,
            @RequestPart("file") MultipartFile file,
            @RequestPart("anonymousFile") MultipartFile anonymousFile,
            @RequestPart(name = "appendixFile", required = false) MultipartFile appendixFile,
            RedirectAttributes redirectAttrs,
            SessionStatus sessionStatus) {
        if (back != null) {
            return "redirect:/submit/step5";
        }
        if (file != null && file.getSize() > 0 && anonymousFile != null && anonymousFile.getSize() > 0) {
            try {
                Article persistArticle = articleService.create(article, file, anonymousFile, appendixFile);
                redirectAttrs.addFlashAttribute("successMsg", "Gửi bài thành công");

                sessionStatus.setComplete();

                return "redirect:/submission/processing/" + persistArticle.getId().toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/submit/step6";
    }

}