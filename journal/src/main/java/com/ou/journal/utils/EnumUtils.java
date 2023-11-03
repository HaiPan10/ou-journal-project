package com.ou.journal.utils;

import com.ou.journal.enums.ArticleStatus;
import com.ou.journal.enums.AuthorType;

public class EnumUtils {
    public static ArticleStatus[] getArticleStatus() {
        return ArticleStatus.values();
    }

    public static AuthorType[] getAuthorTypes() {
        return AuthorType.values();
    }
    
}
