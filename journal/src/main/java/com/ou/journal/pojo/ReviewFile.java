package com.ou.journal.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "review_file")
@NoArgsConstructor
public class ReviewFile implements Serializable{
    @Id
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "content", columnDefinition = "MEDIUMBLOB")
    private byte[] content;

    @Column(name = "size")
    private Long size;

    @Column(name = "type")
    private String type;

    @JsonIgnore
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @MapsId
    @OneToOne(optional = false)
    private ReviewArticle reviewArticle;
}
