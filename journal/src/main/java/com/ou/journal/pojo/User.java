package com.ou.journal.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "{user.firstName.notBlank}")
    @NotNull
    @Size(min = 1, max = 45, message = "{user.firstName.invalidSize}")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "{user.lastName.notBlank}")
    @NotNull
    @Size(min = 1, max = 45, message = "{user.lastName.invalidSize}")
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "dob")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dob;

    @NotBlank(message = "{account.email.notBlank}")
    @Email(message = "{account.email.invalid}")
    @Size(min = 1, message = "{account.email.invalidSize}")
    @Column(name = "email")
    private String email;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "user")
    private Account account;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<UserRole> userRoles;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Article> editorArticles;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<ReviewArticle> reviewArticles;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<AuthorArticle> authorArticles;
}
