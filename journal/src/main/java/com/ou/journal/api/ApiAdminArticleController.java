package com.ou.journal.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.pojo.Article;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.utils.FileConverterUtils;

@RestController
@RequestMapping("admin/articles")
public class ApiAdminArticleController {
    @Autowired
    private ArticleService articleService;

    // @Autowired
    // private MailService mailService;

    @GetMapping("/view/{articleId}")
    public ResponseEntity<byte[]> view(@PathVariable Long articleId) throws Exception {
        Article article = articleService.retrieve(Long.valueOf(articleId));
        byte[] documentData = article.getCurrentManuscript().getContent();
        HttpHeaders headers = new HttpHeaders();
        byte[] htmlData;
        if (article.getCurrentManuscript().getType().equals("application/pdf")) {
            // htmlData = FileConverterUtils.generateHTMLFromPDF(documentData);
            htmlData = documentData;
        } else {
            // byte[] pdfBytes = FileConverterUtils.convertToPDF(documentData);
            // htmlData = FileConverterUtils.generateHTMLFromPDF(pdfBytes);                
            htmlData = FileConverterUtils.convertToPDF(documentData);
        }
        // headers.setContentType(MediaType.TEXT_HTML);
        headers.setContentType(MediaType.APPLICATION_PDF);

        return new ResponseEntity<>(htmlData, headers, HttpStatus.OK);
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

    @PutMapping("/end-review/{articleId}")
    public ResponseEntity<?> endInvitationReview(@PathVariable Long articleId){
        try {
            articleService.endInvitationReview(articleId);
            return ResponseEntity.ok().body("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
