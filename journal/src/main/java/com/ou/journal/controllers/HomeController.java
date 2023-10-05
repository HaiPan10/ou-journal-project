package com.ou.journal.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class HomeController {
    // @ModelAttribute("webName")
    // public String getWebName() {
    //     return "test";
    // }

    @GetMapping("/")
    public String homePage() {
        return "login";
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
