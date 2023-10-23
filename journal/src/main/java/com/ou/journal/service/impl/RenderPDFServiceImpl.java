package com.ou.journal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ou.journal.pojo.Article;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.RenderPDFService;
import com.ou.journal.utils.FileConverterUtils;

@Service
public class RenderPDFServiceImpl implements RenderPDFService{

    @Autowired
    private ArticleService articleService;

    @Override
    public ResponseEntity<byte[]> view(Long articleId) throws Exception {
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

        return new ResponseEntity<>(htmlData, headers, HttpStatus.OK);
    }
    
}
