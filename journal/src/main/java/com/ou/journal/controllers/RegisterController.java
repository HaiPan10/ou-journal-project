package com.ou.journal.controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ou.journal.configs.JwtService;
import com.ou.journal.enums.AccountStatus;
import com.ou.journal.enums.SecrectType;
import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.AccountService;
import com.ou.journal.service.interfaces.UserService;

import jakarta.validation.Valid;

@Controller
public class RegisterController {
    @Autowired
    UserService userService;

    @Autowired
    AccountService accountService;

    @Autowired
    JwtService jwtService;

    @GetMapping("/register")
    public String registerPage(
            @RequestParam(required = false, name = "token") String token, Model model) {
        if (token != null) {
            // Kiểm tra token
            String email = jwtService.getEmailFromToken(token, SecrectType.EMAIL);
            if (email != null) {
                User user = new User();
                Account account = new Account();
                user.setAccount(account);
                user.setEmail(email);
                model.addAttribute("user", user);
                model.addAttribute("token", token);
                return "client/register";
            }
        }
        return "client/registerEmail";
    }

    // Bước kiểm tra email
    @PostMapping("/register/email")
    public String submitEmail(@RequestPart("email") String email, RedirectAttributes redirectAttributes) {
        try {
            if (accountService.getByEmail(email).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email đã được sử dụng, vui lòng nhập email khác");
                return "redirect:/register";
            } else if (userService.findByEmail(email) != null) {
                //Xử lý gửi mail tạo tài khoản
                return "hehe";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String token = jwtService.generateEmailToken(null, null, email);
        System.out.println(token);
        return String.format("redirect:/register?token=%s", token);
    }

    // Người dùng chưa có trong hệ thống gửi thông tin đăng ký
    @PostMapping("/register")
    public String submitRegister(@ModelAttribute("user") @Valid User u, BindingResult rs) {
        if (!rs.hasErrors()) {
            u.getAccount().setEmail(u.getEmail());
            u.getAccount().setStatus(AccountStatus.ACCEPTED.toString());
           //u.setUserRoles()
        }
        return "client/register";
    }

}
