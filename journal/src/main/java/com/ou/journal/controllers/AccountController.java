package com.ou.journal.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ou.journal.pojo.Account;
import com.ou.journal.repository.AccountRepositoryJPA;


@Controller
public class AccountController {
    @Autowired
    private AccountRepositoryJPA accountRepositoryJPA;

    @GetMapping("/accounts")
    public String list(Model model) {
        List<Account> accounts = accountRepositoryJPA.findAll();
        model.addAttribute("accounts", accounts);
        return "userManager";
    }
}
