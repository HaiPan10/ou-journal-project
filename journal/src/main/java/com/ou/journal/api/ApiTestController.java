package com.ou.journal.api;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.enums.AuthorType;
import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.AuthorArticle;
import com.ou.journal.pojo.AuthorRole;
import com.ou.journal.service.interfaces.AccountService;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.AuthorTypeService;
import com.ou.journal.service.interfaces.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/tests")
public class ApiTestController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private AuthorTypeService authorTypeService;

    @PostMapping(path = "/register")
    public ResponseEntity<?> register (@Valid @RequestBody Account account, BindingResult bindingResult) throws Exception {
        try {
            if (bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getAllErrors().get(0).getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(account));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(path = "/verify/{accountId}")
    public ResponseEntity<?> verify (@PathVariable Long accountId, @RequestParam String status) throws Exception {
        try {
            return ResponseEntity.ok(accountService.changeAccountStatus(accountId, status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/upload")
    public ResponseEntity<?> uploadArticle (@RequestPart MultipartFile file, @RequestPart Article article) {
        try {
            // Code dơ để test đầu vào là Article có sẵn thuộc tính từ form-data bên thymeleaf
            // Article article = new Article();
            // AuthorArticle firstAuthorArticle = new AuthorArticle(userService.retrieve(Long.valueOf(3)), article);
            // firstAuthorArticle.setAuthorRoles(new ArrayList<AuthorRole>(
            //     Arrays.asList(
            //         new AuthorRole(
            //             firstAuthorArticle,
            //             authorTypeService.findByType(AuthorType.FIRST_AUTHOR.toString())
            //         )
            //     )
            // ));
            // AuthorArticle correspondingAuthorArticle = new AuthorArticle(userService.retrieve(Long.valueOf(4)), article);
            // correspondingAuthorArticle.setAuthorRoles(new ArrayList<AuthorRole>(
            //     Arrays.asList(
            //         new AuthorRole(
            //             correspondingAuthorArticle,
            //             authorTypeService.findByType(AuthorType.CORRESPONDING_AUTHOR.toString())
            //         )
            //     )
            // ));
            // AuthorArticle authorArticle = new AuthorArticle(userService.retrieve(Long.valueOf(5)), article);
            // authorArticle.setAuthorRoles(new ArrayList<AuthorRole>(
            //     Arrays.asList(
            //         new AuthorRole(
            //             authorArticle,
            //             authorTypeService.findByType(AuthorType.AUTHOR.toString())
            //         )
            //     )
            // ));
            // article.setAuthorArticles(new ArrayList<AuthorArticle>(
            //     Arrays.asList(
            //         firstAuthorArticle,
            //         correspondingAuthorArticle,
            //         authorArticle
            //     )
            // ));
            // // Gán cứng user gọi api
            // // UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // String userName = "phonglai0809";
            // Account account = accountService.findByUserName(userName);

            // article.setTitle(title);
            return ResponseEntity.status(HttpStatus.CREATED).body(articleService.create(article, file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
