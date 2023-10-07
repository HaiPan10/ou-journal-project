package com.ou.journal.service.interfaces;

import com.ou.journal.pojo.AuthorArticle;
import com.ou.journal.pojo.AuthorRole;

public interface AuthorRoleService {
    AuthorRole create(AuthorArticle authorArticle, String roleType) throws Exception;
}
