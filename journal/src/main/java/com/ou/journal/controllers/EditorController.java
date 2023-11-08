package com.ou.journal.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.annotation.Secured;
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
import com.ou.journal.enums.RoleName;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.AuthenticationUser;
import com.ou.journal.pojo.ReviewArticle;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.ReviewArticleService;
import com.ou.journal.service.interfaces.UserService;
import com.ou.journal.utils.EnumUtils;
import com.ou.journal.validator.WebAppValidator;

@Controller
@Secured({ "ROLE_EDITOR", "ROLE_CHIEF_EDITOR" })
public class EditorController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ReviewArticleService reviewArticleService;
    @Autowired
    private UserService userService;
    @Autowired
    private WebAppValidator webAppValidator;

    @ModelAttribute("articleStatusEnum")
    public com.ou.journal.enums.ArticleStatus[] getArticleStatus() {
        return EnumUtils.getArticleStatus();
    }

    @GetMapping("/editor/invite-reviewer-articles")
    public String getArticleWaitingForInviteReviewer(Model model,
            @AuthenticationPrincipal AuthenticationUser currentUser) {
        List<Article> articles = new ArrayList<>();
        articles = articleService.getArticleWaitingForInviteReviewer(currentUser.getId());
        model.addAttribute("articles", articles);

        model.addAttribute("breadcumTitle", "Danh sách bài báo chờ mời phản biện viên");

        return "client/editor/invitingReviewList";
    }

    @GetMapping("/editor/waiting-accept-reviewer-articles")
    public String getArticleWaitingForAcceptFromReviewer(Model model,
            @AuthenticationPrincipal AuthenticationUser currentUser) {
        List<Article> articles = new ArrayList<>();
        articles = articleService.getArticleWaitingForAcceptFromReviewer(currentUser.getId());
        model.addAttribute("articles", articles);

        model.addAttribute("breadcumTitle", "Danh sách bài báo chờ chưa có phản biện viên");

        return "client/editor/invitingReviewList";
    }

    @GetMapping("/editor/review-articles/invite/{articleId}")
    public String viewReviewer(Model model, @PathVariable Long articleId,
            @AuthenticationPrincipal AuthenticationUser currentUser) throws Exception {
        try {
            Article article = articleService.retrieve(articleId, currentUser.getId());
            if (article.getStatus().equals(ArticleStatus.INVITING_REVIEWER.toString()) ||
                    article.getStatus().equals(ArticleStatus.IN_REVIEW.toString())) {
                List<ReviewArticle> reviewArticles = reviewArticleService.findByArticle(articleId);
                List<ReviewArticle> olderReviewArticle = reviewArticleService.findByOlderManuscript(articleId);
                Map<User, List<ReviewArticle>> reviewers = new HashMap<>();
                if(olderReviewArticle.size() != 0){
                    olderReviewArticle.stream().forEach(ra -> {
                        List<ReviewArticle> ras = reviewers.get(ra.getUser());
                        if(ras == null){
                            ras = new ArrayList<>();
                            reviewers.put(ra.getUser(), ras);
                        }
                        ras.add(ra);
                    });
                }
                model.addAttribute("reviewers", reviewers);
                model.addAttribute("reviewArticles", reviewArticles);
                model.addAttribute("articleId", articleId);
                model.addAttribute("article", article);
                List<Object[]> users = userService.listUser();
                model.addAttribute("users", users);
                User user = new User();
                model.addAttribute("user", user);
            } else {
                model.addAttribute("error", "in valid status");
            }
            return "client/editor/articleReviewerManager";
        } catch (Exception e) {
            // model.addAttribute("error", e.getMessage());
            return "redirect:/main-menu";
        }
    }

    @PostMapping(path = "/editor/review-articles/invite/{articleId}")
    public String inviteReviewer(@ModelAttribute("user") User user, @PathVariable Long articleId,
            Model model, BindingResult bindingResult) throws Exception {
        Article article = articleService.retrieve(articleId);
        List<ReviewArticle> reviewArticles = reviewArticleService.findByArticle(articleId);
        List<Object[]> users = userService.listUser();
        try {
            webAppValidator.validate(user, bindingResult);
            if (bindingResult.hasErrors()) {
                model.addAttribute("article", article);
                model.addAttribute("reviewArticles", reviewArticles);
                model.addAttribute("articleId", articleId);
                model.addAttribute("users", users);
                return "client/editor/articleReviewerManager";
            }
            reviewArticleService.create(user, article);
            return "redirect:/editor/review-articles/invite/{articleId}";
        } catch (Exception e) {
            model.addAttribute("article", article);
            model.addAttribute("reviewArticles", reviewArticles);
            model.addAttribute("articleId", articleId);
            model.addAttribute("users", users);
            bindingResult.addError(new ObjectError("exceptionError", e.getMessage()));
            return "client/editor/articleReviewerManager";
        }
    }

    @GetMapping("/editor/assign-list")
    public String getAssignList(Model model) {
        try {
            List<Article> articles = articleService.list(ArticleStatus.ASSIGN_EDITOR.toString());
            model.addAttribute("articles", articles);
        } catch (Exception e) {
            model.addAttribute("articles", new ArrayList<Article>());
        }
        return "client/editor/assignList";
    }

    @Secured("ROLE_CHIEF_EDITOR")
    @GetMapping(path = "/editor/assign-list/{articleId}")
    public String getMethodName(@PathVariable Long articleId, Model model) {
        String status = articleService.getArticleStatusById(articleId);
        if (status == null || !status.equals(ArticleStatus.ASSIGN_EDITOR.toString())) {
            return "redirect:/main-menu";
        }
        model.addAttribute("editors", userService.findByRoleName(RoleName.ROLE_EDITOR.toString()));
        model.addAttribute("articleId", articleId);
        return "client/editor/assignEditorList";
    }

    @GetMapping("/editor/in-review-articles")
    public String getInReviewArticle(Model model, @AuthenticationPrincipal AuthenticationUser currentUser) {
        try {
            List<Article> articles = articleService.getInReviewArticles(currentUser.getId());
            model.addAttribute("articles", articles);
        } catch (Exception e) {
            model.addAttribute("articles", new ArrayList<Article>());
        }

        model.addAttribute("breadcumTitle", "Danh sách bài báo đang được phản biện");

        return "client/editor/decidingList";
    }

    @GetMapping("/editor/reviewed-articles")
    public String getReviewedArticle(Model model, @AuthenticationPrincipal AuthenticationUser currentUser) {
        try {
            List<Article> articles = articleService.getReviewedArticles(currentUser.getId());
            model.addAttribute("articles", articles);
        } catch (Exception e) {
            model.addAttribute("articles", new ArrayList<Article>());
        }

        model.addAttribute("breadcumTitle", "Danh sách bài báo đã phản biện");

        return "client/editor/decidingList";
    }

    @GetMapping("/editor/review/{articleId}")
    public String decideArticle(Model model,
            @PathVariable Long articleId,
            @AuthenticationPrincipal AuthenticationUser currentUser,
            @RequestParam(name = "back", required = false) String back) {
        try {
            Article article = articleService.retrieve(articleId, currentUser.getId());
            if (!article.getStatus().equals(ArticleStatus.IN_REVIEW.toString())) {
                return "redirect:/editor/deciding-list";
            }
            List<ReviewArticle> reviewArticles = reviewArticleService.findByArticle(articleId);
            model.addAttribute("articleId", articleId);
            model.addAttribute("reviewArticles", reviewArticles);
            model.addAttribute("viewUrl", String.format("/api/articles/view/%s", article.getId()));
            model.addAttribute("article", article);

            if (back != null) {
                switch (back) {
                    case "assign-list":
                        model.addAttribute("urlBack", "/editor/assign-list");
                        model.addAttribute("backTitle", "Danh sách bài báo chờ gán biên tập viên");
                        break;
                    case "assigned-list":
                        model.addAttribute("urlBack", "/editor/assigned-list");
                        model.addAttribute("backTitle", "Danh sách bài báo được gán biên tập viên");
                        break;
                    case "invite-reviewer-articles":
                        model.addAttribute("urlBack", "/editor/invite-reviewer-articles");
                        model.addAttribute("backTitle", "Danh sách bài báo chờ mời phản biện viên");
                        break;
                    case "waiting-accept-reviewer-articles":
                        model.addAttribute("urlBack", "/editor/waiting-accept-reviewer-articles");
                        model.addAttribute("backTitle", "Danh sách bài báo chờ chưa có phản biện viên");
                        break;
                    case "in-review-articles":
                        model.addAttribute("urlBack", "/editor/in-review-articles");
                        model.addAttribute("backTitle", "Danh sách bài báo đang được phản biện");
                        break;
                    case "reviewed-articles":
                        model.addAttribute("urlBack", "/editor/reviewed-articles");
                        model.addAttribute("backTitle", "Danh sách bài báo đã phản biện");
                        break;
                }
            }
            return "client/editor/decideArticle";
        } catch (Exception e) {
            // model.addAttribute("error", e.getMessage());
            return "redirect:/main-menu";
        }
    }

    @GetMapping("/editor/assigned-list")
    public String assignedArticle(Model model, @AuthenticationPrincipal AuthenticationUser user) {
        model.addAttribute("articles", articleService.findByEditorUserId(user.getId()));
        return "client/editor/assignedList";
    }
}
