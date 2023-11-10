package com.ou.journal.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ou.journal.enums.ReviewArticleStatus;
import com.ou.journal.pojo.EditorFile;
import com.ou.journal.pojo.Manuscript;
import com.ou.journal.pojo.ReviewArticle;
import com.ou.journal.service.interfaces.EditorFileService;
import com.ou.journal.service.interfaces.ManuscriptService;
import com.ou.journal.service.interfaces.RenderPDFService;
import com.ou.journal.service.interfaces.ReviewArticleService;
import com.ou.journal.utils.FileConverterUtils;

@Service
public class RenderPDFServiceImpl implements RenderPDFService{

    @Autowired
    private ManuscriptService manuscriptService;

    @Autowired
    private ReviewArticleService reviewArticleService;

    @Autowired
    private EditorFileService editorFileService;

    @Override
    public ResponseEntity<byte[]> viewAuthorFile(Long articleId, String version) throws Exception {
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
        byte[] originData = renderManuscript.getContent();
        byte[] documentData;
        HttpHeaders headers = new HttpHeaders();
        // byte[] htmlData;
        if (renderManuscript.getType().equals("application/pdf")) {
            // htmlData = FileConverterUtils.generateHTMLFromPDF(documentData);
            documentData = originData;
        } else {
            // byte[] pdfBytes = FileConverterUtils.convertToPDF(documentData);
            // htmlData = FileConverterUtils.generateHTMLFromPDF(pdfBytes);
            documentData = FileConverterUtils.convertToPDF(originData);
        }
        // headers.setContentType(MediaType.TEXT_HTML);
        headers.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(documentData, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> viewReviewerFile(Long reviewArticleId) throws Exception {
        ReviewArticle reviewArticle = reviewArticleService.retrieve(reviewArticleId);
        if (!reviewArticle.getStatus().equals(ReviewArticleStatus.ACCEPT_PUBLISH.toString()) &&
        !reviewArticle.getStatus().equals(ReviewArticleStatus.REJECT_PUBLISH.toString())) {
            throw new Exception("Phản biện này không hợp lệ để xem file!");
        }
        byte[] originData = reviewArticle.getReviewFile().getContent();
        byte[] documentData;
        HttpHeaders headers = new HttpHeaders();
        if (reviewArticle.getReviewFile().getType().equals("application/pdf")) {
            documentData = originData;
        } else {
            documentData = FileConverterUtils.convertToPDF(originData);
        }

        headers.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(documentData, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> viewEditorFile(Long editorFileId) throws Exception {
        EditorFile editorFile = editorFileService.retrieve(editorFileId);
        byte[] originData = editorFile.getContent();
        byte[] documentData;
        HttpHeaders headers = new HttpHeaders();
        if (editorFile.getType().equals("application/pdf")) {
            documentData = originData;
        } else {
            documentData = FileConverterUtils.convertToPDF(originData);
        }

        headers.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(documentData, headers, HttpStatus.OK);
    }
}
