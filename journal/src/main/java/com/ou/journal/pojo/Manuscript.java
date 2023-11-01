package com.ou.journal.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "manuscript")
@NoArgsConstructor
public class Manuscript implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // @NotNull
    // @Column(name = "url")
    // private String url;

    @Lob
    @Column(name = "content", columnDefinition = "MEDIUMBLOB")
    private byte[] content;

    @Column(name = "size")
    private Long size;

    @NotNull
    @Column(name = "version")
    private String version;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "type")
    private String type;

    @JsonIgnore
    @JoinColumn(name = "article_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Article article;

    // @JsonIgnore
    // @OneToMany(cascade = CascadeType.ALL, mappedBy = "manuscript")
    // private List<ReviewCriteria> reviewCriterias;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "manuscript")
    private List<ReviewArticle> reviewArticles;

    public Manuscript(byte[] content, Long size, @NotNull String version, Date createdDate, String type, Article article) {
        this.content = content;
        this.size = size;
        this.version = version;
        this.createdDate = createdDate;
        this.type = type;
        this.article = article;
    }

    
}
