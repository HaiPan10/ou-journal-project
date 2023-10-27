package com.ou.journal.api;

import java.net.URI;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ou.journal.components.UserSessionInfo;
import com.ou.journal.configs.JwtService;
import com.ou.journal.enums.ReviewArticleStatus;
import com.ou.journal.enums.SecrectType;
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
    private UserSessionInfo userSessionInfo;

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
    public ResponseEntity<?> reviewerResponseReview(@PathVariable Long reviewArticleId, @RequestParam String status) throws Exception{
        if (status.equals(ReviewArticleStatus.ACCEPTED.toString()) || status.equals(ReviewArticleStatus.REJECTED.toString())) {
            try {
                return ResponseEntity.ok().body(reviewArticleService.changeReviewStatus(reviewArticleId, status, 
                userSessionInfo.getCurrentAccount().getEmail(), userSessionInfo.getCurrentAccount().getId()));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ!");
        }
    }

    @PutMapping("/reviewer/review/{reviewArticleId}")
    public ResponseEntity<?> doneReview(@PathVariable Long reviewArticleId) throws Exception{
        try {
            return ResponseEntity.ok().body(reviewArticleService.doneReview(reviewArticleId, userSessionInfo.getCurrentAccount().getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
