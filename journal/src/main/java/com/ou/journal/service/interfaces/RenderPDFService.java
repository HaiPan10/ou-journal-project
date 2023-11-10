package com.ou.journal.service.interfaces;

import org.springframework.http.ResponseEntity;

public interface RenderPDFService {
    ResponseEntity<byte[]> viewAuthorFile(Long articleId, String version) throws Exception;
    ResponseEntity<byte[]> viewReviewerFile(Long reviewArticleId) throws Exception;
    ResponseEntity<byte[]> viewEditorFile(Long editorFileId) throws Exception;
}
