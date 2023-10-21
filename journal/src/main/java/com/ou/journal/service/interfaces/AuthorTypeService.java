package com.ou.journal.service.interfaces;

import com.ou.journal.pojo.AuthorType;

public interface AuthorTypeService {
    AuthorType findByType(String type) throws Exception;
}
