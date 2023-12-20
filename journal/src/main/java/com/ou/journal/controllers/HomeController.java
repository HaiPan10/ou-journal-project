package com.ou.journal.controllers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ou.journal.enums.RoleName;
import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.ArticleCategory;
import com.ou.journal.pojo.UserRole;
import com.ou.journal.service.interfaces.AccountService;
import com.ou.journal.service.interfaces.ArticleCategoryService;

@Controller
public class HomeController {
    @Autowired
    AccountService accountService;
    // @ModelAttribute("webName")
    // public String getWebName() {
    // return "test";S
    // }

    @Autowired
    private ArticleCategoryService articleCategoryService;

    @GetMapping("/login")
    public String userLoginPage() {
        return "login";
    }

    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "adminLogin";
    }

    @GetMapping("/profile")
    public String profilePage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            try {
                Account account = accountService.findByUserName(userDetails.getUsername());
                model.addAttribute("account", account);

                // Lấy ra danh sách các Role từ Account
                Set<UserRole> userRoles = account.getUser().getUserRoles();

                // Chuyển đổi từ UserRole sang RoleName và lấy ra displayName
                String roles = userRoles.stream().map(userRole -> {
                    for (RoleName role : RoleName.values()) {
                        if (role.name().equals(userRole.getRole().getRoleName())) {
                            return role.getDisplayName();
                        }
                    }
                    return "";
                }).collect(Collectors.joining(", "));

                model.addAttribute("roles", roles);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "client/profile";
    }

    @GetMapping("/admin/dashboard")
    public String homepage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            System.out.println("[DEBUG] - " + userDetails.getUsername());
        }
        return "dashboard";
    }

    @GetMapping({ "/homepage", "/" })
    public String clientHomepage(Model model) {
        List<ArticleCategory> cates = articleCategoryService.findAll();
        model.addAttribute("cates", cates);
        return "client/homepage";
    }
    // @GetMapping({"/submit", "/submit-step1"})
    // public String submitPage() {
    // return "client/submitManuscript/step1";
    // }

}
