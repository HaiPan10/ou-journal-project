package com.ou.journal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ou.journal.pojo.AuthorArticle;
import com.ou.journal.pojo.AuthorRole;
import com.ou.journal.pojo.AuthorType;
import com.ou.journal.repository.AuthorRoleRepositoryJPA;
import com.ou.journal.service.interfaces.AuthorRoleService;
import com.ou.journal.service.interfaces.AuthorTypeService;

@Service
public class AuthorRoleServiceImpl implements AuthorRoleService {
    @Autowired
    private AuthorRoleRepositoryJPA authorRoleRepositoryJPA;
    @Autowired
    private AuthorTypeService authorTypeService;

    @Override
    public AuthorRole create(AuthorArticle authorArticle, String roleType) throws Exception {
        try {
            AuthorType authorType = authorTypeService.findByType(roleType);
            AuthorRole authorRole = new AuthorRole(authorArticle, authorType);
            return authorRoleRepositoryJPA.save(authorRole);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
}
