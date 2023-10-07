package com.ou.journal.pojo;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "author_role")
@NoArgsConstructor
public class AuthorRole implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "author_id", referencedColumnName = "id")
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
