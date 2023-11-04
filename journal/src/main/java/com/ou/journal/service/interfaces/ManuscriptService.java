package com.ou.journal.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.pojo.AuthorNote;
import com.ou.journal.pojo.Manuscript;

public interface ManuscriptService {
    Manuscript getLastestManuscript(Long articleId);
    Manuscript reUpManuscript(Long articleId, MultipartFile manuscripFile, AuthorNote authorNote) throws Exception;
}
