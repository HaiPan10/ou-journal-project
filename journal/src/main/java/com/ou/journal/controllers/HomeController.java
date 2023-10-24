package com.ou.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ou.journal.service.interfaces.AccountService;
import com.ou.journal.service.interfaces.UserService;

@Controller
public class HomeController {
    // @ModelAttribute("webName")
    // public String getWebName() {
    // return "test";
    // }

    @GetMapping("/login")
    public String userLoginPage() {
        return "login";
    }

    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin_login";
    }

    @GetMapping("/admin/dashboard")
    public String homepage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            System.out.println("[DEBUG] - " + userDetails.getUsername());
        }
        return "dashboard";
    }

    @GetMapping("/homepage")
    public String clientHomepage() {
        return "client/homepage";
    }
    // @GetMapping({"/submit", "/submit-step1"})
    // public String submitPage() {
    // return "client/submitManuscript/step1";
    // }

}
