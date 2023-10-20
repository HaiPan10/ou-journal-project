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
            htmlData = FileConverterUtils.generateHTMLFromPDF(documentData);
        } else {
            byte[] pdfBytes = FileConverterUtils.convertToPDF(documentData);
            htmlData = FileConverterUtils.generateHTMLFromPDF(pdfBytes);
        }
        headers.setContentType(MediaType.TEXT_HTML);

        // MailRequest mailRequest = new MailRequest("phongvulai96@gmail.com", "subject", "body");
        // Context context = new Context();
        // context.setVariable("subject", mailRequest.getSubject());
        // context.setVariable("body", mailRequest.getBody());

        // mailService.sendEmailWithHtmlTemplate(mailRequest.getTo(), mailRequest.getSubject(), "mail/index", context);

        return new ResponseEntity<>(htmlData, headers, HttpStatus.OK);
    }

    @PutMapping("/accept/{articleId}")
    public ResponseEntity<?> acceptArticle(@PathVariable Long articleId, @RequestBody Article article){
        try {
            articleService.updateArticleStatus(articleId, article, ArticleStatus.ACCEPT.toString());
            return ResponseEntity.ok().body("Cập nhật thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/reject/{articleId}")
    public ResponseEntity<?> rejectArticle(@PathVariable Long articleId, @RequestBody Article article){
        try {
            articleService.updateArticleStatus(articleId, article, ArticleStatus.REJECT.toString());
            return ResponseEntity.ok().body("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
