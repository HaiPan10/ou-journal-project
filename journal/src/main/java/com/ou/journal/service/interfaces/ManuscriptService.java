package com.ou.journal.service.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.AuthorNote;
import com.ou.journal.pojo.Manuscript;

public interface ManuscriptService {
    Manuscript getLastestManuscript(Long articleId);

    Manuscript reUpManuscript(Long articleId, MultipartFile manuscripFile, MultipartFile anonymousFile,
            MultipartFile appendixFile, String reference, AuthorNote authorNote) throws Exception;

    List<Manuscript> findByArticle(Article article);

    Optional<Manuscript> getById(Long id);

    Optional<Manuscript> findByArticleAndVersion(Long articleId, String version);
}
