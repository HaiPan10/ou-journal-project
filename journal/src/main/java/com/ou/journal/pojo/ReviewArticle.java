package com.ou.journal.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

    @Column(name = "invited_at")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date invitedAt;

    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "reviewArticle")
    private List<ReviewCriteria> reviewCriterias;

    @Column(name = "status")
    private String status;
}
