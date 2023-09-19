package com.ou.journal.pojo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "author_article")
public class AuthorArticle implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "author_id", referencedColumnName = "id")
    @ManyToOne
    private User user;

    @JoinColumn(name = "article_id", referencedColumnName = "id")
    @ManyToOne
    private Article article;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "authorArticle")
    private List<AuthorRole> authorRoles;
}
