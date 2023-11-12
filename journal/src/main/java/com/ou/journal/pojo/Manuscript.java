package com.ou.journal.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
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
    @Column(name = "content", columnDefinition = "LONGBLOB")
    private byte[] content;

    @Lob
    @Column(name = "content_anonymous", columnDefinition = "LONGBLOB")
    private byte[] contentAnonymous;

    @Column(name = "name")
    private String name;

    @Column(name = "name_anonymous")
    private String nameAnonymous;

    @NotNull
    @Column(name = "version")
    private String version;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "type")
    private String type;

    @Column(name = "type_anonymous")
    private String typeAnonymous;

    @JsonIgnore
    @JoinColumn(name = "article_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Article article;

    // @JsonIgnore
    // @OneToMany(cascade = CascadeType.ALL, mappedBy = "manuscript")
    // private List<ReviewCriteria> reviewCriterias;

    @JsonIgnore
    @OneToMany(cascade = { CascadeType.REMOVE }, mappedBy = "manuscript")
    private List<ReviewArticle> reviewArticles;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "manuscript")
    private Appendix appendix;

    @Column(name = "reference", columnDefinition = "TEXT")
    private String reference;

    @JsonIgnore
    @OneToMany(cascade = { CascadeType.REMOVE}, mappedBy = "manuscript")
    private List<EditorFile> editorFiles;

    @Transient
    private MultipartFile file;

    @Transient
    private MultipartFile anonymousFile;

    @Transient
    private MultipartFile appendixFile;

    public Manuscript(byte[] content, String name, @NotNull String version, Date createdDate, String type,
            byte[] contentAnonymous, String nameAnonymous, String typeAnonymous,
            String reference, Article article) {
        this.content = content;
        this.name = name;
        this.version = version;
        this.createdDate = createdDate;
        this.type = type;
        this.reference = reference;
        this.article = article;
        this.contentAnonymous = contentAnonymous;
        this.nameAnonymous = nameAnonymous;
        this.typeAnonymous = typeAnonymous;
    }

    public Manuscript(byte[] content, String name, @NotNull String version, Date createdDate, String type,
            String reference, Article article) {
        this.content = content;
        this.name = name;
        this.version = version;
        this.createdDate = createdDate;
        this.type = type;
        this.reference = reference;
        this.article = article;
    }

}
