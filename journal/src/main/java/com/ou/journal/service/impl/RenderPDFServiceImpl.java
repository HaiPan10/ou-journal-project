package com.ou.journal.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.Manuscript;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.ManuscriptService;
import com.ou.journal.service.interfaces.RenderPDFService;
import com.ou.journal.utils.FileConverterUtils;

@Service
public class RenderPDFServiceImpl implements RenderPDFService{

    @Autowired
    private ManuscriptService manuscriptService;

    @Override
    public ResponseEntity<byte[]> view(Long articleId, String version) throws Exception {
        Manuscript renderManuscript;
        if (version == null) {
            renderManuscript = manuscriptService.getLastestManuscript(articleId);
        } else {
            Optional<Manuscript> manuscriptOptional = manuscriptService.findByArticleAndVersion(articleId, version);
            if (manuscriptOptional.isPresent()) {
                renderManuscript = manuscriptOptional.get();
            } else {
                renderManuscript = manuscriptService.getLastestManuscript(articleId);
            }
        }
        byte[] documentData = renderManuscript.getContent();
        HttpHeaders headers = new HttpHeaders();
        // byte[] htmlData;
        if (renderManuscript.getType().equals("application/pdf")) {
            // htmlData = FileConverterUtils.generateHTMLFromPDF(documentData);
        } else {
            // byte[] pdfBytes = FileConverterUtils.convertToPDF(documentData);
            // htmlData = FileConverterUtils.generateHTMLFromPDF(pdfBytes);
            documentData = FileConverterUtils.convertToPDF(documentData);
        }
        // headers.setContentType(MediaType.TEXT_HTML);
        headers.setContentType(MediaType.APPLICATION_PDF);

        return new ResponseEntity<>(documentData, headers, HttpStatus.OK);
    }
    
}
