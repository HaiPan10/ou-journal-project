package com.ou.journal.api;

import java.net.URI;

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
import com.ou.journal.enums.RoleName;
import com.ou.journal.enums.SecrectType;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.RenderPDFService;
import com.ou.journal.service.interfaces.UserRoleService;

@RestController
@RequestMapping("/api/articles")
public class ApiArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserSessionInfo userSessionInfo;

    @Autowired
    private RenderPDFService renderPDFService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private Environment environment;

    @GetMapping("/view/{articleId}")
    public ResponseEntity<byte[]> view(@PathVariable Long articleId) {
        try {
            return renderPDFService.view(articleId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/editor/decide/{articleId}")
    public ResponseEntity<?> decideArticle(@PathVariable Long articleId, @RequestParam String status) throws Exception{
        try {
            if (userRoleService.getByUserAndRoleName(userSessionInfo.getCurrentAccount().getUser(), 
            RoleName.ROLE_EDITOR.toString()).isPresent()) {
                return ResponseEntity.ok().body(articleService.editorDecide(articleId, status));
            } else {
                return ResponseEntity.badRequest().body("Bạn không có quyền thực hiện hành động này!");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/author/article/withdraw/{articleId}")
    public ResponseEntity<?> withdrawArticle(@PathVariable Long articleId) throws Exception{
        try {
            return ResponseEntity.ok().body(articleService.widthdrawArticle(articleId,
             userSessionInfo.getCurrentAccount().getUser().getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/author/article/withdraw")
    public ResponseEntity<?> withdrawArticle(@RequestParam String token) throws Exception{
        try {
            Long id = jwtService.getUserIdFromToken(token, SecrectType.EMAIL);
            Long articleId = jwtService.getArticleIdFromToken(token, SecrectType.EMAIL);

            articleService.widthdrawArticle(articleId, id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(String.format("%s/article-action/success?token=%s", environment.getProperty("SERVER_HOSTNAME"), token)));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        } catch (Exception e) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(String.format("%s/article-action/failed?token=%s&reason=%s", environment.getProperty("SERVER_HOSTNAME"), token, e.getMessage())));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        }
    }
}
