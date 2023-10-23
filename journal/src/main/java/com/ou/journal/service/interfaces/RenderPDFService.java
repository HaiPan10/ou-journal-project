package com.ou.journal.service.interfaces;

import org.springframework.http.ResponseEntity;

public interface RenderPDFService {
    ResponseEntity<byte[]> view(Long articleId) throws Exception;
}
