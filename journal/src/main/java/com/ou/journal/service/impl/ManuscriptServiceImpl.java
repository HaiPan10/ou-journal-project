package com.ou.journal.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.AuthorNote;
import com.ou.journal.pojo.Manuscript;
import com.ou.journal.repository.ManuscriptRepositoryJPA;
import com.ou.journal.service.interfaces.ArticleService;
import com.ou.journal.service.interfaces.AuthorNoteService;
import com.ou.journal.service.interfaces.ManuscriptService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ManuscriptServiceImpl implements ManuscriptService {
    @Autowired
    private ManuscriptRepositoryJPA manuscriptRepositoryJPA;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private AuthorNoteService authorNoteService;

    @Override
    public Manuscript getLastestManuscript(Long articleId) {
        return manuscriptRepositoryJPA.getLastestManuscript(articleId);
    }

    @Override
    public Manuscript reUpManuscript(Long articleId, MultipartFile manuscripFile, String reference, AuthorNote authorNote) throws Exception {
        Article article = articleService.retrieve(articleId);
        if (article.getStatus().equals(ArticleStatus.REJECT.toString()) || article.getStatus().equals(ArticleStatus.SECRETARY_REJECT.toString()) ||
        article.getStatus().equals(ArticleStatus.WITHDRAW.toString())) {
            double latestVersion = Double.parseDouble(manuscriptRepositoryJPA.getLastestVersion(articleId));
            Manuscript manuscript = new Manuscript(
                manuscripFile.getBytes(), 
                manuscripFile.getSize(), 
                Double.toString(latestVersion + 1), 
                new Date(), 
                manuscripFile.getContentType(),
                reference,
                article);
            authorNoteService.create(authorNote, article);
            return manuscriptRepositoryJPA.save(manuscript);
        } else {
            throw new Exception("Trạng thái bài đăng không hợp lệ!");
        }
    }

    @Override
    public List<Manuscript> findByArticle(Article article) {
        return manuscriptRepositoryJPA.findByArticle(article);
    }

    @Override
    public Optional<Manuscript> getById(Long id) {
        return manuscriptRepositoryJPA.findById(id);
    }

    @Override
    public Optional<Manuscript> findByArticleAndVersion(Long articleId, String version) {
        return manuscriptRepositoryJPA.findByArticleAndVersion(articleId, version);
    }
    
}
