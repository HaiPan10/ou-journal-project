package com.ou.journal.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ou.journal.pojo.Article;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.RenderPDFService;

@RestController
@RequestMapping("admin/articles")
public class ApiAdminArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private RenderPDFService renderPDFService;

    // @Autowired
    // private MailService mailService;

    @GetMapping("/view/{articleId}")
    public ResponseEntity<byte[]> view(@PathVariable Long articleId) throws Exception {
        try {
            return renderPDFService.view(articleId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // @PutMapping("/in-review/{articleId}")
    // public ResponseEntity<?> acceptArticle(@PathVariable Long articleId, @RequestBody Article article){
    //     try {
    //         articleService.updateArticleStatus(articleId, article, ArticleStatus.IN_REVIEW.toString());
    //         return ResponseEntity.ok().body("Cập nhật thành công");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.badRequest().body(e.getMessage());
    //     }
    // }

    @PutMapping("/verify/{articleId}")
    public ResponseEntity<?> verifyArticle(@PathVariable Long articleId, @RequestBody Article article){
        try {
            articleService.updateArticleStatus(articleId, article);
            return ResponseEntity.ok().body("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
