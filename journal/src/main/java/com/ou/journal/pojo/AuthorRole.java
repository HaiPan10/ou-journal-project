package com.ou.journal.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "author_role")
@NoArgsConstructor
public class AuthorRole implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @JoinColumn(name = "author_article_id", referencedColumnName = "id")
    @ManyToOne
    private AuthorArticle authorArticle;

    @JoinColumn(name = "author_type_id", referencedColumnName = "id")
    @ManyToOne
    private AuthorType authorType;

    public AuthorRole(AuthorArticle authorArticle, AuthorType authorType) {
        this.authorArticle = authorArticle;
        this.authorType = authorType;
    }

    
}
