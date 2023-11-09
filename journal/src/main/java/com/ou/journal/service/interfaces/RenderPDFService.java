package com.ou.journal.service.interfaces;

import org.springframework.http.ResponseEntity;

public interface RenderPDFService {
    ResponseEntity<byte[]> view(Long articleId, String version) throws Exception;
    ResponseEntity<byte[]> view(Long reviewArticleId) throws Exception;
}
