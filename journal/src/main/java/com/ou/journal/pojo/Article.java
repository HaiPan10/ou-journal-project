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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "article")
@NoArgsConstructor
public class Article implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "{article.title.notBlank}")
    @NotNull
    @Size(min = 1, max = 255, message = "{artical.title.invalidSize}")
    @Column(name = "title")
    private String title;

    @Column(name = "status")
    private String status;

    @Column(name = "keywords")
    private String keywords;

    @Column(name = "abstract")
    private String abstracts;

    @JoinColumn(name = "editor_id", referencedColumnName = "id")
    @ManyToOne
    private User editorUser;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "article")
    private List<Manuscript> manuscripts;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "article")
    private List<ArticleDate> articleDates;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "article")
    private List<AuthorArticle> authorArticles;

    @Transient
    private Manuscript currentManuscript;
}
