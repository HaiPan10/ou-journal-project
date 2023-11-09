package com.ou.journal.api;

import java.net.URI;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.configs.JwtService;
import com.ou.journal.enums.ReviewArticleStatus;
import com.ou.journal.enums.SecrectType;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.AuthenticationUser;
import com.ou.journal.pojo.User;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.ReviewArticleService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/review-articles")
public class ApiReviewArticleController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private ReviewArticleService reviewArticleService;

    @Autowired
    private Environment environment;

    @Autowired
    private ArticleService articleService;

    @GetMapping("/response")
    public ResponseEntity<?> responseReview(@RequestParam String status, @RequestParam String token, HttpServletRequest httpServletRequest) throws Exception{
        if (status.equals(ReviewArticleStatus.ACCEPTED.toString()) || status.equals(ReviewArticleStatus.REJECTED.toString())) {
            try {
                Long id = jwtService.getUserIdFromToken(token, SecrectType.EMAIL);
                String email = jwtService.getEmailFromToken(token, SecrectType.EMAIL);
                Long reviewArticleId = jwtService.getReviewArticleIdFromToken(token, SecrectType.EMAIL);
                reviewArticleService.changeReviewStatus(reviewArticleId, status, email, id);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(URI.create(String.format("%s/reviewer-invite/success?token=%s", environment.getProperty("SERVER_HOSTNAME"), token)));
                return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
            } catch (AccountNotFoundException e) {
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(URI.create(String.format("%s/reviewer-invite/create?token=%s", environment.getProperty("SERVER_HOSTNAME"), token)));
                return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
            } catch (Exception e) {
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(URI.create(String.format("%s/reviewer-invite/create?token=%s", environment.getProperty("SERVER_HOSTNAME"), token)));
                return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
            }
        } else {
            return null;
        }
    }

    @PutMapping("/reviewer/{reviewArticleId}/response")
    public ResponseEntity<?> reviewerResponseReview(@PathVariable Long reviewArticleId, 
        @RequestParam String status, @AuthenticationPrincipal AuthenticationUser currentUser) throws Exception{
        if (status.equals(ReviewArticleStatus.ACCEPTED.toString()) || status.equals(ReviewArticleStatus.REJECTED.toString())) {
            try {
                return ResponseEntity.ok().body(reviewArticleService.changeReviewStatus(reviewArticleId, status, 
                currentUser.getEmail(), currentUser.getId()));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ!");
        }
    }

    @PostMapping("/reviewer/review/{reviewArticleId}")
    public ResponseEntity<?> doneReview(Model model, @PathVariable Long reviewArticleId,
        MultipartFile reviewFile, String status, @AuthenticationPrincipal AuthenticationUser currentUser) throws Exception{
        try {
            return ResponseEntity.ok().body(reviewArticleService.doneReview(reviewArticleId, currentUser.getId(), reviewFile, status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reviewer/reinvite/{articleId}")
    public ResponseEntity<?> reinviteReviewer(@PathVariable Long articleId, @RequestBody User user){
        try {
            Article article = articleService.retrieve(articleId);
            return ResponseEntity.ok().body(reviewArticleService.create(user, article));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
