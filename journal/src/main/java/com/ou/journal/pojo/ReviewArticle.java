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
@Table(name = "review_article")
@NoArgsConstructor
public class ReviewArticle implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "reviewer_id", referencedColumnName = "id")
    @ManyToOne
    private User user;

    @JoinColumn(name = "article_id", referencedColumnName = "id")
    @ManyToOne
    private Manuscript manuscript;

    @Column(name = "comment")
    private String comment;
}
