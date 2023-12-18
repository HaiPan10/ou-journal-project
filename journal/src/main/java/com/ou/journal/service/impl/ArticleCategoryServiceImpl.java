package com.ou.journal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ou.journal.pojo.ArticleCategory;
import com.ou.journal.repository.ArticleCategoryRepositoryJPA;
import com.ou.journal.service.interfaces.ArticleCategoryService;

@Service
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    @Autowired
    private ArticleCategoryRepositoryJPA categoryRepository;
    @Override
    public List<ArticleCategory> findAll() {
        return categoryRepository.findAll();
    }

}
