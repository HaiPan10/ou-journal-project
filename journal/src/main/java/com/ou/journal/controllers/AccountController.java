package com.ou.journal.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.ou.journal.pojo.Account;
import com.ou.journal.service.interfaces.AccountService;


@Controller
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/admin/accounts")
    public String list(Model model) {
        List<Account> accounts = accountService.findAll();
        model.addAttribute("accounts", accounts);
        return "userManager";
    }

    // Dẫn ra trang account, nếu account id không tồn tại hiện lỗi (đã đc throw sẵn từ api) ra ngoài front end
    @GetMapping("/admin/accounts/{accountId}")
    public String retrieve(Model model, @PathVariable Long accountId) throws Exception {
        try {
            Account account = accountService.retrieve(accountId);
            model.addAttribute("account", account);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        // Front end chưa có !!!
        return "emDungemMaiemTcuaHai";
    }

    // Gọi thông qua th:href="@{/accounts/verify/{id}(id=${p.id},status='REJECT')}" phía front end qua thẻ <a></a>
    @GetMapping("/admin/accounts/verify/{accountId}")
    public String verify(@PathVariable Long accountId, @RequestParam String status) throws Exception {
        if (accountService.changeAccountStatus(accountId, status)) {
            return "redirect:/accounts/{accountId}?status=success";
        } else {
            return "redirect:/accounts/{accountId}?status=fail";
        }
    }

}
