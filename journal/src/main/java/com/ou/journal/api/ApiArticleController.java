package com.ou.journal.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ou.journal.service.interfaces.RenderPDFService;

@RestController
@RequestMapping("/api/articles")
public class ApiArticleController {
    // @Autowired
    // private ArticleService articleService;

    @Autowired
    private RenderPDFService renderPDFService;

    @GetMapping("/view/{articleId}")
    public ResponseEntity<byte[]> view(@PathVariable Long articleId) {
        try {
            return renderPDFService.view(articleId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
