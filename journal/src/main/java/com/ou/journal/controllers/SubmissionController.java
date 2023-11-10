package com.ou.journal.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.AuthenticationUser;
import com.ou.journal.pojo.AuthorNote;
import com.ou.journal.pojo.Manuscript;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.ManuscriptService;
import com.ou.journal.utils.EnumUtils;

@Controller
public class SubmissionController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private ManuscriptService manuscriptService;

    @ModelAttribute("articleStatusEnum")
    public com.ou.journal.enums.ArticleStatus[] getArticleStatus() {
        return EnumUtils.getArticleStatus();
    }

    @ModelAttribute("authorTypesEnum")
    public com.ou.journal.enums.AuthorType[] getTypes() {
        return EnumUtils.getAuthorTypes();
    }

    @GetMapping("/submission/processing")
    public String processedSubmission(Model model, @AuthenticationPrincipal AuthenticationUser currentUser) {
        try {
            List<Article> articles = articleService.findByAuthorId(currentUser.getId());
            model.addAttribute("articles", articles);
            System.out.println("[DEBUG] - Finish Fetching");
        } catch (Exception e) {
            model.addAttribute("articles", new ArrayList<Article>());
            System.out.println("[DEBUG] - Catch exception: " + e.getMessage());
        }

        return "client/author/processingSubmission";
    }

    @GetMapping("/submission/processing/{articleId}")
    public String processedSubmissionDetail(Model model, @PathVariable Long articleId,
            @RequestParam(required = false) String version) {
        try {
            Article article = articleService.retrieve(articleId);
            model.addAttribute("viewUrl", String.format("/api/articles/view/%s", article.getId()));
            model.addAttribute("article", article);
            if (article.getStatus().equals(ArticleStatus.REJECT.toString())) {
                model.addAttribute("editorFiles", manuscriptService.getLastestManuscript(article.getId()).getEditorFiles());
            }

            // truy vấn manuscript theo phiên bản
            model.addAttribute("manuscripts", manuscriptService.findByArticle(article));

            Manuscript renderManuscript;
            if (version == null) {
                renderManuscript = manuscriptService.getLastestManuscript(articleId);
            } else {
                Optional<Manuscript> manuscriptOptional = manuscriptService.findByArticleAndVersion(articleId, version);
                if (manuscriptOptional.isPresent()) {
                    renderManuscript = manuscriptOptional.get();
                } else {
                    return "redirect:/admin/articles/{articleId}";
                }
            }
            model.addAttribute("renderManuscript", renderManuscript);

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "client/author/articleDetail";
    }

    @GetMapping("/submission/processing/{articleId}/re-submit")
    public String reSubmit(Model model, @PathVariable Long articleId) {
        try {
            Article article = articleService.retrieve(articleId);
            if (!article.getStatus().equals("SECRETARY_REJECT") && !article.getStatus().equals("REJECT")) {
                return "redirect:/submission/processing/{articleId}";
            }

            model.addAttribute("article", article);
            model.addAttribute("authorNote", new AuthorNote());

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "client/author/resubmitManuscript";
    }

    @PostMapping("/submission/processing/{articleId}/re-submit")
    public String submitPage5(Model model, @PathVariable Long articleId,
            @ModelAttribute("authorNote") AuthorNote authorNote,
            @RequestPart("reference") String reference,
            @RequestPart("file") MultipartFile file,
            @RequestPart("anonymousFile") MultipartFile anonymousFile,
            @RequestPart("appendixFile") MultipartFile appendixFile,
            RedirectAttributes redirectAttrs) {

        try {
            if (manuscriptService.reUpManuscript(articleId, file, anonymousFile, appendixFile, reference,
                    authorNote) != null)
                redirectAttrs.addFlashAttribute("successMsg", "Gửi lại bài thành công");

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "client/author/resubmitManuscript";
        }

        return "redirect:/submission/processing/{articleId}";
    }
}
