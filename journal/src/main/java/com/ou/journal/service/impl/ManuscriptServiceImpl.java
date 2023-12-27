package com.ou.journal.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.pojo.Appendix;
import com.ou.journal.pojo.Article;
import com.ou.journal.pojo.AuthorNote;
import com.ou.journal.pojo.Manuscript;
import com.ou.journal.repository.ManuscriptRepositoryJPA;
import com.ou.journal.service.interfaces.AppendixService;
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
    @Autowired
    private AppendixService appendixService;

    @Override
    public Manuscript getLastestManuscript(Long articleId) {
        return manuscriptRepositoryJPA.getLastestManuscript(articleId);
    }

    @Override
    public Manuscript reUpManuscript(Long articleId, MultipartFile manuscripFile, MultipartFile anonymousFile,
            MultipartFile appendixFile, String reference, AuthorNote authorNote) throws Exception {
        Article article = articleService.retrieve(articleId);
        if (article.getStatus().equals(ArticleStatus.REJECT.toString())
                || article.getStatus().equals(ArticleStatus.SECRETARY_REJECT.toString()) ||
                article.getStatus().equals(ArticleStatus.WITHDRAW.toString())) {

            if(reference == null || reference.trim().isEmpty()){
                throw new Exception("Trích dẫn không hợp lệ");
            }
            
            double latestVersion = Double.parseDouble(manuscriptRepositoryJPA.getLastestVersion(articleId));
            Manuscript manuscript = new Manuscript(
                    manuscripFile.getBytes(),
                    manuscripFile.getName(),
                    Double.toString(latestVersion + 1),
                    new Date(),
                    manuscripFile.getContentType(),
                    anonymousFile.getBytes(),
                    anonymousFile.getName(),
                    anonymousFile.getContentType(),
                    reference.trim(),
                    article);
            authorNoteService.create(authorNote, article);
            manuscriptRepositoryJPA.save(manuscript);

            if(appendixFile != null && !appendixFile.isEmpty() && appendixFile.getSize() > 0){
                Appendix transientAppendix = new Appendix();
                transientAppendix.setManuscript(manuscript);
                transientAppendix.setContent(appendixFile.getBytes());
                transientAppendix.setSize(appendixFile.getSize());
                transientAppendix.setType(appendixFile.getContentType());
                appendixService.create(transientAppendix);
            }

            return manuscript;
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

    @Override
    public Manuscript updateAnonymousFile(MultipartFile anonymousFile, Long articleId) throws IOException, Exception {
        Manuscript manuscript = getLastestManuscript(articleId);
        if(manuscript.getArticle().getEditorUser() == null){
            String msg = "This article don't have editor";
            System.out.println("[DEBUG] - "  + msg);
            throw new Exception(msg);
        }
        manuscript.setContentAnonymous(anonymousFile.getBytes());
        manuscript.setTypeAnonymous(anonymousFile.getContentType());
        return manuscriptRepositoryJPA.save(manuscript);
    }
}
