package com.ou.journal.service.interfaces;

import com.ou.journal.pojo.Manuscript;

public interface ManuscriptService {
    Manuscript getLastestManuscript(Long articleId);
}
