package com.ou.journal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.ou.journal.components.UserSessionInfo;
import com.ou.journal.pojo.Account;
import com.ou.journal.service.interfaces.AccountService;

@ControllerAdvice
@Controller
@PropertySources({
        @PropertySource(value = "classpath:information.yml", encoding = "UTF-8")
})
public class GlobalControllerAdvice {
    @Autowired
    private AccountService accountService;

    @Autowired
    private UserSessionInfo userSessionInfo;

    @Value("${schoolName}")
    private String schoolName;

    @Value("${webName}")
    private String webName;

    @Value("${address}")
    private String address;

    @Value("${publishingAgency}")
    private String publishingAgency;

    @Value("${editor}")
    private String editor;

    @Value("${editorialSecretary}")
    private String editorialSecretary;

    @Value("${emailEditorialSecretary}")
    private String emailEditorialSecretary;

    @ModelAttribute("schoolName")
    public String getSchoolName() {
        return schoolName;
    }

    @ModelAttribute("webName")
    public String getWebName() {
        return webName;
    }

    @ModelAttribute("address")
    public String getAddress() {
        return address;
    }

    @ModelAttribute("publishingAgency")
    public String getPublishingAgency() {
        return publishingAgency;
    }

    @ModelAttribute("editor")
    public String getEditor() {
        return editor;
    }

    @ModelAttribute("editorialSecretary")
    public String[] getEditorialSecretary() {
        return editorialSecretary.split(",");
    }

    @ModelAttribute("emailEditorialSecretary")
    public String[] getEmailEditorialSecretary() {
        return emailEditorialSecretary.split(",");
    }

    @ModelAttribute("userAvatar")
    public String getAvatar(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()){
            String username = authentication.getName();
            try {
                Account account = accountService.findByUserName(username);
                return account.getAvatar();
            } catch (Exception e) {
                
            }
        }
        return null;
    }

    @ModelAttribute("currentAccount")
    public Account getCurrentAccountFromSession(){
        try {
            return userSessionInfo.getCurrentAccount();
        } catch (Exception e) {
            return null;
        }
    }
}
