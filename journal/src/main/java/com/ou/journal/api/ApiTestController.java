package com.ou.journal.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.ou.journal.pojo.Account;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.AuthRequest;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.AccountService;
import com.ou.journal.service.interfaces.ArticleDateService;
import com.ou.journal.service.interfaces.ArticleService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/tests")
public class ApiTestController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleDateService articleDateService;

    @PostMapping(path = "/register")
    public ResponseEntity<?> register(@Valid @RequestBody Account account, BindingResult bindingResult)
            throws Exception {
        try {
            if (bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getAllErrors().get(0).getDefaultMessage());
            }
            User user = account.getUser();
            account.setUser(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(account, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(path = "/verify/{accountId}")
    public ResponseEntity<?> verify(@PathVariable Long accountId, @RequestParam String status) throws Exception {
        try {
            return ResponseEntity.ok(accountService.changeAccountStatus(accountId, status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/upload")
    public ResponseEntity<?> uploadArticle(
            @RequestPart("file") MultipartFile file,
            @RequestPart("anonymousFile") MultipartFile anonymousFile,
            @RequestPart("appendixFile") MultipartFile appendixFile,
            @RequestPart("article") Article article) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(articleService.create(article, file, anonymousFile, appendixFile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/generate-token")
    public ResponseEntity<?> generateToken(@RequestBody AuthRequest authRequest) {
        try {
            System.out.println("[DEBUG] - " + authRequest);
            return ResponseEntity.ok().body(accountService.login(authRequest));
        } catch (Exception e) {
            System.out.println("[DEBUG] - EXCEPTION: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(path = "/article-dates/{articleId}")
    public ResponseEntity<?> getArticleDates(@PathVariable Long articleId) {
        try {
            return ResponseEntity.ok().body(articleDateService.getArticleDates(articleId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
