package com.ou.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ou.journal.service.interfaces.AccountService;
import com.ou.journal.service.interfaces.UserService;

@Controller
public class RegisterController {
    @Autowired
    UserService userService;

    @Autowired
    AccountService accountService;

     @GetMapping("/register")
    public String registerPage(
        @RequestParam(required = false, name = "token") String token) {
        if (token == null)
            return "client/registerEmail";
        else {
            //Kiểm tra token 
            //Hợp lệ return
             return "client/register";
             //Khong hop lẹe thì return "client/registerEmail";
        }
    }

    @PostMapping("/register")
    public String submitRegister(@RequestBody String email, RedirectAttributes redirectAttributes) throws Exception {
        //Kiểm tra email
        if(accountService.getByEmail(email) != null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email đã được sử dụng, vui lòng nhập email khác");
            return "redirect:/register";
        } else if (userService.findByEmail(email) != null) {
            //Xử lý gửi mail
            return "hehe";
        }
        
        //Tạo token
        //
        //
        //Tạo token giả bộ
        String token = "tokenne";

         return String.format("redirect:/client/registerEmail?token=%s", token);
    }
}
