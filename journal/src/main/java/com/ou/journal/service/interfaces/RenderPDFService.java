package com.ou.journal.service.interfaces;

import org.springframework.http.ResponseEntity;

public interface RenderPDFService {
    ResponseEntity<byte[]> viewArticle(Long articleId, String version) throws Exception;
    ResponseEntity<byte[]> viewReviewerFile(Long reviewArticleId) throws Exception;
    ResponseEntity<byte[]> viewEditorFile(Long editorFileId) throws Exception;
    ResponseEntity<byte[]> viewAnonymous(Long articleId, String version) throws Exception;
}
