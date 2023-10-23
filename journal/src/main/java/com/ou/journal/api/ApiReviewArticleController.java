package com.ou.journal.api;

import java.net.URI;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/accept")
    public ResponseEntity<?> acceptReview(@RequestParam String token, HttpServletRequest httpServletRequest) throws Exception{
        try {
            Long id = jwtService.getUserIdFromToken(token, SecrectType.EMAIL);
            String email = jwtService.getEmailFromToken(token, SecrectType.EMAIL);
            Long reviewArticleId = jwtService.getReviewArticleIdFromToken(token, SecrectType.EMAIL);
            reviewArticleService.changeReviewStatus(reviewArticleId, ReviewArticleStatus.ACCEPTED.toString(), email, id);
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
    }

    @GetMapping("/reject")
    public ResponseEntity<?> rejectReview(@RequestParam String token, HttpServletRequest httpServletRequest) throws Exception{
        try {
            Long id = jwtService.getUserIdFromToken(token, SecrectType.EMAIL);
            String email = jwtService.getEmailFromToken(token, SecrectType.EMAIL);
            Long reviewArticleId = jwtService.getReviewArticleIdFromToken(token, SecrectType.EMAIL);
            reviewArticleService.changeReviewStatus(reviewArticleId, ReviewArticleStatus.REJECTED.toString(), email, id);
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
    }
}
