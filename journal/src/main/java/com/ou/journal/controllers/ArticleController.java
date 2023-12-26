package com.ou.journal.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.Manuscript;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.ManuscriptService;

@Controller
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private ManuscriptService manuscriptService;

    @Secured({"ROLE_ADMIN"})
    @GetMapping("/admin/articles")
    public String list(Model model,
            @RequestParam(name = "status", required = false, defaultValue = "PENDING") String status) {
        List<Article> articles = new ArrayList<>();
        model.addAttribute("status", status);
        articles = articleService.list(status);
        model.addAttribute("articles", articles);
        return "articleManager";
    }

    @Secured({ "ROLE_EDITOR", "ROLE_CHIEF_EDITOR", "ROLE_SECRETARY" })
    @GetMapping({"/secretary/articles", "/editor/articles"})
    public String listAritcles(Model model,
            @RequestParam(name = "status", required = false, defaultValue = "PENDING") String status) {
        List<Article> articles = new ArrayList<>();
        model.addAttribute("status", status);
        articles = articleService.list(status);
        model.addAttribute("articles", articles);
        return "client/articleManager";
    }



    @GetMapping("/admin/articles/{articleId}")
    public String retrieve(Model model, @PathVariable Long articleId, @RequestParam(required = false) String version)
            throws Exception {
        try {
            Article article = articleService.retrieve(articleId);
            if (article.getStatus().equals(ArticleStatus.PENDING.toString())) {
                model.addAttribute("article", article);
                model.addAttribute("manuscripts", manuscriptService.findByArticle(article));
                Manuscript renderManuscript;
                if (version == null) {
                    renderManuscript = manuscriptService.getLastestManuscript(articleId);
                } else {
                    Optional<Manuscript> manuscriptOptional = manuscriptService.findByArticleAndVersion(articleId,
                            version);
                    if (manuscriptOptional.isPresent()) {
                        renderManuscript = manuscriptOptional.get();
                    } else {
                        return "redirect:/admin/articles/{articleId}";
                    }
                }
                model.addAttribute("renderManuscript", renderManuscript);
                model.addAttribute("viewUrl", String.format("/api/articles/view/%s?version=%s", article.getId(), renderManuscript.getVersion()));
                model.addAttribute("anonymousUrl", String.format("/api/articles/view-anonymous/%s?version=%s", article.getId(),  renderManuscript.getVersion()));
                model.addAttribute("appendixUrl", String.format("/api/articles/view-appendix/%s?version=%s", article.getId(),  renderManuscript.getVersion()));
            } else {
                model.addAttribute("error", "invalid status");
            }

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "secretaryDecide";
    }

    @Secured({ "ROLE_EDITOR", "ROLE_CHIEF_EDITOR", "ROLE_SECRETARY" })
    @GetMapping({"/secretary/articles/{articleId}", "/editor/articles/{articleId}"})
    public String retrieveAritcle(Model model, @PathVariable Long articleId, @RequestParam(required = false) String version)
            throws Exception {
        try {
            Article article = articleService.retrieve(articleId);
            if (article.getStatus().equals(ArticleStatus.PENDING.toString())) {
                model.addAttribute("article", article);
                model.addAttribute("manuscripts", manuscriptService.findByArticle(article));
                Manuscript renderManuscript;
                if (version == null) {
                    renderManuscript = manuscriptService.getLastestManuscript(articleId);
                } else {
                    Optional<Manuscript> manuscriptOptional = manuscriptService.findByArticleAndVersion(articleId,
                            version);
                    if (manuscriptOptional.isPresent()) {
                        renderManuscript = manuscriptOptional.get();
                    } else {
                        return "redirect:/main-menu";
                    }
                }
                model.addAttribute("renderManuscript", renderManuscript);
                model.addAttribute("viewUrl", String.format("/api/articles/view/%s?version=%s", article.getId(), renderManuscript.getVersion()));
                model.addAttribute("anonymousUrl", String.format("/api/articles/view-anonymous/%s?version=%s", article.getId(),  renderManuscript.getVersion()));
                model.addAttribute("appendixUrl", String.format("/api/articles/view-appendix/%s?version=%s", article.getId(),  renderManuscript.getVersion()));
            } else {
                model.addAttribute("error", "invalid status");
            }

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "client/articleApproval";
    }
}
