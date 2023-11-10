package com.ou.journal.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "editor_file")
@NoArgsConstructor
public class EditorFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "content", columnDefinition = "LONGBLOB")
    private byte[] content;

    @Column(name = "type")
    private String type;

    @Column(name = "name")
    private String name;

    @ManyToOne
    private Manuscript manuscript;

    public EditorFile(byte[] content, String type, String name, Manuscript manuscript) {
        this.content = content;
        this.type = type;
        this.name = name;
        this.manuscript = manuscript;
    }

    
}
