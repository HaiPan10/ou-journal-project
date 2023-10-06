package com.ou.journal.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    // @ModelAttribute("webName")
    // public String getWebName() {
    //     return "test";
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
    public String homepage() {
        return "userManager";
    }

    @GetMapping("/submit")
    public String submitPage() {
        return "client/submitManuscript/step1";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "client/register";
    }
}
