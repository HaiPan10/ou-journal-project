package com.ou.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ou.journal.configs.JwtService;
import com.ou.journal.enums.RoleName;
import com.ou.journal.enums.SecrectType;
import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.ReviewArticle;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.ReviewArticleService;
import com.ou.journal.service.interfaces.UserService;
import com.ou.journal.validator.WebAppValidator;

@Controller
public class AnonymousController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private Environment environment;
    @Autowired
    private ReviewArticleService reviewArticleService;
    @Autowired
    private UserService userService;
    @Autowired
    private WebAppValidator webAppValidator;

    @GetMapping("/error-page")
    public String errorPage(Model model) {
        model.addAttribute("home", String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
        model.addAttribute("error", "Token của bạn không hợp lệ! Vui lòng không giả mạo token!");
        return "anonymous/errorPage";
    }

    @GetMapping("/reviewer-invite/create")
    public String accountInfo(Model model, @RequestParam String token) throws Exception {
        try {
            Long id = jwtService.getUserIdFromToken(token, SecrectType.EMAIL);
            String email = jwtService.getEmailFromToken(token, SecrectType.EMAIL);
            Long reviewArticleId = jwtService.getReviewArticleIdFromToken(token, SecrectType.EMAIL);
            reviewArticleService.checkArticleAvailable(reviewArticleId, email, id);
            Account account = new Account();
            account.setEmail(email);
            model.addAttribute("account", account);
            model.addAttribute("reviewArticleId", reviewArticleId);
            model.addAttribute("token", token);
            model.addAttribute("targetEndpoint",
                String.format("%s/reviewer-invite/create?token=%s",
                environment.getProperty("SERVER_HOSTNAME"), token));
            return "anonymous/accountInfo";
        } catch (NullPointerException e) {
            model.addAttribute("home", String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
            model.addAttribute("error", "Token của bạn không hợp lệ! Vui lòng không giả mạo token!");
            return "anonymous/errorPage";
        } catch (Exception e) {
            model.addAttribute("home", String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
            model.addAttribute("error", e.getMessage());
            return "anonymous/errorPage";
        }
    }

    @PostMapping("/reviewer-invite/create")
    public String createAccountAndAcceptInvitation(Model model, @ModelAttribute("account") Account account,
            @RequestParam String token, BindingResult bindingResult) throws Exception {
        try {
            Long id = jwtService.getUserIdFromToken(token, SecrectType.EMAIL);
            String email = jwtService.getEmailFromToken(token, SecrectType.EMAIL);
            Long reviewArticleId = jwtService.getReviewArticleIdFromToken(token, SecrectType.EMAIL);
            reviewArticleService.checkArticleAvailable(reviewArticleId, email, id);
            webAppValidator.validate(account, bindingResult);
            if (bindingResult.hasErrors()) {
                model.addAttribute("token", token);
                model.addAttribute("targetEndpoint",
                    String.format("%s/reviewer-invite/create?token=%s",
                    environment.getProperty("SERVER_HOSTNAME"), token));
                return "anonymous/accountInfo";
            }
            ReviewArticle reviewArticle = reviewArticleService.acceptReviewAndCreateAccount(reviewArticleId, email, id, account);
            String targetEndpoint = String.format("reviewer/review/%s", reviewArticleId);
            String loginToken = jwtService.generateMailLoginToken(reviewArticle.getUser(), "reviewArticleId", reviewArticleId,
            RoleName.ROLE_REVIEWER.toString(), targetEndpoint);
            return String.format("redirect:/api/accounts/login?token=%s", loginToken);
        } catch (Exception e) {
            bindingResult.addError(new ObjectError("exceptionError", e.getMessage()));
            model.addAttribute("token", token);
            return "anonymous/accountInfo";
        }
    }

    @GetMapping("/reviewer-invite/success")
    public String invitationResponseSuccess(Model model, @RequestParam String token) throws Exception {
        try {
            Long userId = jwtService.getUserIdFromToken(token, SecrectType.EMAIL);
            String email = jwtService.getEmailFromToken(token, SecrectType.EMAIL);
            Long reviewArticleId = jwtService.getReviewArticleIdFromToken(token, SecrectType.EMAIL);
            if (reviewArticleService.checkResponseRevviewArticleInvitation(reviewArticleId, userId, email)) {
                model.addAttribute("home", String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
                User user = userService.retrieve(userId);
                model.addAttribute("user", String.format("%s %s", user.getLastName(), user.getLastName()));
                model.addAttribute("action", "TỪ CHỐI lời mời phản biện bài báo");
                return "anonymous/responseConfirmation";
            } else {
                model.addAttribute("home", String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
                model.addAttribute("error", "Phản hồi của bạn không hợp lệ! Vui lòng không tùy chỉnh đường dẫn!");
                return "anonymous/errorPage";
            }
        } catch (Exception e) {
            model.addAttribute("home", String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
            model.addAttribute("error", "Token của bạn không hợp lệ! Vui lòng không giả mạo token!");
            return "anonymous/errorPage";
        }
    }

    @GetMapping("/article-action/success")
    public String articleActionSuccess(Model model, @RequestParam String token) throws Exception {
        try {
            Long userId = jwtService.getUserIdFromToken(token, SecrectType.EMAIL);
            jwtService.getArticleIdFromToken(token, SecrectType.EMAIL);
            User user = userService.retrieve(userId);
            model.addAttribute("home", String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
            model.addAttribute("user", String.format("%s %s", user.getLastName(), user.getLastName()));
            model.addAttribute("action", "hành động rút bài đăng");
            return "anonymous/responseConfirmation";
        } catch (Exception e) {
            model.addAttribute("home", String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
            model.addAttribute("error", "Token của bạn không hợp lệ! Vui lòng không giả mạo token!");
            return "anonymous/errorPage";
        }
    }

    @GetMapping("/article-action/failed")
    public String articleActionFailed(Model model, @RequestParam String token, @RequestParam String reason) throws Exception {
        try {
            jwtService.getUserIdFromToken(token, SecrectType.EMAIL);
            jwtService.getArticleIdFromToken(token, SecrectType.EMAIL);
            model.addAttribute("home", String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
            model.addAttribute("error", String.format("Hành động rút bài không hợp lệ. Lý do: %s", reason));
            return "anonymous/errorPage";
        } catch (Exception e) {
            model.addAttribute("home", String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
            model.addAttribute("error", "Token của bạn không hợp lệ! Vui lòng không giả mạo token!");
            return "anonymous/errorPage";
        }
    }
}
