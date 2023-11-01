package com.ou.journal.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "user")
@NoArgsConstructor
// @ToString(exclude = {"account"})
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @NotBlank(message = "{user.email.notBlank}")
    @Email(message = "{user.email.invalid}")
    @Size(min = 1, message = "{user.email.invalidSize}")
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @JsonIgnore
    @Valid
    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "user")
    private Account account;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER)
    private Set<UserRole> userRoles;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "editorUser")
    private List<Article> editorArticles;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<ReviewArticle> reviewArticles;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<AuthorArticle> authorArticles;

    @Override
    public String toString() {
        return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", dob=" + dob + ", email="
                + email + "]";
    }
}
