package com.ou.journal.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "review_criteria")
@AllArgsConstructor
public class ReviewCriteria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @JoinColumn(name = "review_article_id", referencedColumnName = "id")
    @ManyToOne
    private ReviewArticle reviewArticle;

    @Column(name = "criteria_name")
    private String criteriaName;

    @Column(name = "criteria_value ")
    private String criteriaValue ;
}
