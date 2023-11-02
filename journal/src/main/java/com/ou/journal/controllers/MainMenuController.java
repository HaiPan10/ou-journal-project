package com.ou.journal.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ou.journal.enums.RoleName;
import com.ou.journal.pojo.AuthenticationUser;
import com.ou.journal.pojo.UserRole;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.UserRoleService;
import com.ou.journal.service.interfaces.UserService;

@Controller
public class MainMenuController {
    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @GetMapping("main-menu")
    public String mainMenu(@AuthenticationPrincipal AuthenticationUser currentUser, Model model) throws Exception {
        Optional<UserRole> editorUserOptional = userRoleService.getByUserAndRoleName(
            userService.retrieve(currentUser.getId()), RoleName.ROLE_EDITOR.toString());
        if (editorUserOptional.isPresent()) {
            model.addAttribute("articleWaitingForInviteReviewCount", 
                articleService.getArticleWaitingForInviteReviewer(currentUser.getId()).size());
            model.addAttribute("articleWaitingForAcceptFromReviewerCount", 
                articleService.getArticleWaitingForAcceptFromReviewer(currentUser.getId()).size());  
            model.addAttribute("articleInReviewingCount", 
                articleService.getInReviewArticles(currentUser.getId()).size());
            model.addAttribute("articleReviewedCount", 
                articleService.getReviewedArticles(currentUser.getId()).size());
        }  
        return "client/mainMenu";
    }
}
