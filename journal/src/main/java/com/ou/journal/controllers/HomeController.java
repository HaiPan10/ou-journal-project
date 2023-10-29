package com.ou.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.AccountService;

@Controller
public class HomeController {
    @Autowired
    AccountService accountService;
    // @ModelAttribute("webName")
    // public String getWebName() {
    // return "test";S
    // }

    @GetMapping("/login")
    public String userLoginPage() {
        return "login";
    }

    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin_login";
    }

    @GetMapping("/profile")
    public String profilePage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            try {
                Account account = accountService.findByUserName(userDetails.getUsername());
                model.addAttribute("account", account);
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
    public String clientHomepage() {
        return "client/homepage";
    }
    // @GetMapping({"/submit", "/submit-step1"})
    // public String submitPage() {
    // return "client/submitManuscript/step1";
    // }

}
