package com.ou.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nimbusds.jose.JOSEException;
import com.ou.journal.configs.JwtService;
import com.ou.journal.enums.SecrectType;
import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.AccountService;
import com.ou.journal.service.interfaces.MailService;
import com.ou.journal.service.interfaces.UserService;
import com.ou.journal.validator.WebAppValidator;

@Controller
public class RegisterController {
    @Autowired
    UserService userService;

    @Autowired
    AccountService accountService;

    @Autowired
    JwtService jwtService;

    @Autowired
    MailService mailService;

    @Autowired
    private WebAppValidator webAppValidator;

    @Autowired
    private Environment environment;

    @GetMapping("/register")
    public String registerPage(
            @RequestParam(required = false, name = "token") String token, Model model,
            RedirectAttributes redirectAttributes) {
        if (token != null) {
            // Kiểm tra token
            String email = jwtService.getEmailFromToken(token, SecrectType.REGISTER);
            if (email != null) {
                Account account = new Account();
                account.setEmail(email);
                model.addAttribute("token", token);
                User user = new User();
                user.setEmail(email);
                user.setAccount(account);
                model.addAttribute("user", user);
                return "client/register";
            } else {
                model.addAttribute("home",
                    String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
                model.addAttribute("error", "Token không hợp lệ");
                return "anonymous/errorPage";
            }
        }
        return "client/registerEmail";
    }

    // Bước kiểm tra email
    @PostMapping("/register/email")
    public String submitEmail(@RequestPart("email") String email, RedirectAttributes redirectAttributes) {
        try {
            User user = null;
            if (accountService.getByEmail(email).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Email đã được sử dụng, vui lòng nhập email khác");
            } else if ((user = userService.findByEmail(email)) != null) {
                mailService.sendCreateAccountMail(user);
                redirectAttributes.addFlashAttribute("sendMailMessage", "Vui lòng xác thực email!");
            }
            return "redirect:/register";
        } catch (Exception e) {
            System.out.println("[ERROR] - Message: " + e.getMessage());
        }
        String token = jwtService.generateToken(email);
        System.out.println("[DEBUG] - Token: " + token);
        return String.format("redirect:/register?token=%s", token);
    }

    // Người dùng chưa có trong hệ thống gửi thông tin đăng ký
    @PostMapping("/register")
    public String submitRegister(@ModelAttribute("user") User user, BindingResult rs,
            @RequestParam(required = false, name = "token") String token, Model model,
            RedirectAttributes redirectAttributes) {
        webAppValidator.validate(user, rs);
        if (!rs.hasErrors()) {
            try {
                String email = jwtService.getEmailFromToken(token, SecrectType.REGISTER);
                System.out.println("[DEBUG] - " + email);
                if (email == null || !email.equals(user.getEmail()) || !email.equals(user.getAccount().getEmail())) {
                    throw new JOSEException("Token không hợp lệ");
                }
                Account account = user.getAccount();
                user.setAccount(null);
                accountService.create(account, user);
                redirectAttributes.addFlashAttribute("registrySuccess", "Đăng ký thành công");
                return "redirect:/login";
            } catch (JOSEException e) {
                System.out.println("[ERROR] - Message: " + e.getMessage());
                model.addAttribute("home",
                    String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
                model.addAttribute("error", e.getMessage());
                return "anonymous/errorPage";
            } catch (Exception e) {
                System.out.println("[ERROR] - Message: " + e.getMessage());
            }

        } else {
            rs.getAllErrors().forEach(e -> {
                System.out.println("[DEBUG] - " + e.getDefaultMessage());
            });
        }
        model.addAttribute("token", token);
        return "client/register";
    }

    @GetMapping("/register/account")
    public String registerPage(@RequestParam(required = false, name = "token") String token, Model model) {
        Long id = jwtService.getIdFromToken(token, SecrectType.EMAIL);
        String email = jwtService.getEmailFromToken(token, SecrectType.EMAIL);
        if (token != null && id != null && email != null) {
            Account account = new Account();
            account.setEmail(email);
            model.addAttribute("account", account);
            model.addAttribute("targetEndpoint",
                    String.format("%s/register/account?token=%s", environment.getProperty("SERVER_HOSTNAME"), token));
            return "anonymous/accountInfo";
        }
        model.addAttribute("home", String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
        model.addAttribute("error", "Token của bạn không hợp lệ! Vui lòng không giả mạo token!");
        return "anonymous/errorPage";
    }

    @PostMapping("/register/account")
    public String submitRegister(@ModelAttribute("account") Account account, BindingResult rs,
            @RequestParam(required = false, name = "token") String token, Model model) {
        webAppValidator.validate(account, rs);
        if (!rs.hasErrors()) {
            try {
                Long id = jwtService.getIdFromToken(token, SecrectType.EMAIL);
                String email = jwtService.getEmailFromToken(token, SecrectType.EMAIL);
                if(token == null || id == null || email == null || !email.equals(account.getEmail())){
                    throw new JOSEException("Token không hợp lệ");
                }
                accountService.create(account, id);
                return "redirect:/login";
            } catch(JOSEException e){
                System.out.println("[ERROR] - Message: " + e.getMessage());
                model.addAttribute("home",
                    String.format("%s", environment.getProperty("SERVER_HOSTNAME")));
                model.addAttribute("error", e.getMessage());
                return "anonymous/errorPage";
            } catch (Exception e) {
                System.out.println("[ERROR] - Message: " + e.getMessage());
            }
        } 
        model.addAttribute("targetEndpoint",
                String.format("%s/register/account?token=%s", environment.getProperty("SERVER_HOSTNAME"), token));
        return "client/registerAccount";
    }

}
