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

    /**
     * The controller will return the register page for user to fill account and user information.
     * Depend on the token as the request param this controller will choose different view.
     * @param token a token string determine if the user information is stored in database or not
     * @param model Model
     * @param redirectAttributes RedirectAttributes
     * @return "client/register" view if the token is valid for a new user. Otherwise, return "client/registerEmail"
     * string if the token is null or invalid so the user will have to fill the email for the server to check.
     */
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
    /**
     * This controller will receive user email to check in the database if the submitted email is
     * already existed on server. If the email is already linked to existed account, it'll prevent
     * user registry this email. Otherwise, the server will send confirm email to user
     * or generate a new token used for creating a new account.
     * @param email The requested user email for checking
     * @param redirectAttributes RedirectAttributes
     * @return a string "redirect:/register" if the email is existed. Otherwise, return a "redirect:/register?token="
     * string with request param is a JWT token. See also, the controller
     * {@link RegisterController#registerPage(String, Model, RedirectAttributes)}
     * after the user have already submitted the email.
     */
    @PostMapping("/register/email")
    public String submitEmail(@RequestPart("email") String email, RedirectAttributes redirectAttributes) {
        try {
            User user = null;
            // Check is email is linked to an account
            if (accountService.getByEmail(email).isPresent()) {
                // the email is already linked to an account, prevent user
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Email đã được sử dụng, vui lòng nhập email khác");
            } else if ((user = userService.findByEmail(email)) != null) {
                // send confirm email for user
                mailService.sendCreateAccountMail(user);
                redirectAttributes.addFlashAttribute("sendMailMessage", "Vui lòng xác thực email!");
            }
            return "redirect:/register";
        } catch (Exception e) {
            System.out.println("[ERROR] - Message: " + e.getMessage());
        }
        // the email doesn't existed on the server, create a new account
        String token = jwtService.generateToken(email);
        System.out.println("[DEBUG] - Token: " + token);
        return String.format("redirect:/register?token=%s", token);
    }

    // Người dùng chưa có trong hệ thống gửi thông tin đăng ký
    /**
     * This controller is used for email doesn't existed on the server. Creating a new account.
     * @param user {@link User} pojo included all the user information and a nested {@link Account} pojo.
     * @param rs BindingResult
     * @param token a token string ensure the email isn't existed on the server.
     * @param model Model
     * @param redirectAttributes RedirectAttributes
     * @return a "redirect:/login" string after the user registry successfully. Otherwise, a
     * "client/register" string to let the user submit the registry form again.
     */
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

    /**
     * This controller is used for after the user click on the confirm email link,
     * require a token as the request param to ensure the registry process only for the user
     * information is already stored on server but don't have an account yet. 
     * @param token a token string ensure the user request is correctly
     * @param model Model
     * @return a "anonymous/accountInfo" string if the token is valid to let the user fill the account information.
     * Otherwise, return a "anonymous/errorPage" string when received the invalid token.
     */
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

    /**
     * This controller is used when the user is submitted from
     * {@link RegisterController#registerPage(String, Model)}
     * to create a new account linked with the requested email.
     * @param account {@link Account} pojo contains user's account information
     * @param rs BindingResult
     * @param token a token string ensure the request is valid
     * @param model Model
     * @return a "client/registerAccount" string if create account successfully. Otherwise, return a
     * "anonymous/errorPage" string if the token is invalid.
     */
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
