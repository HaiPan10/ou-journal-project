package com.ou.journal.pojo;

import java.io.Serializable;
import java.util.Date;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "account")
public class Account implements Serializable {
    @Id
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "{user.firstName.notBlank}")
    @NotNull
    @Size(min = 1, max = 45, message = "{account.userName.invalidSize}")
    @Column(name = "user_name")
    private String userName;

    @NotBlank(message = "{account.email.notBlank}")
    @Email(message = "{account.email.invalid}")
    @Size(min = 1, message = "{account.email.invalidSize}")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "{account.password.notBlank}")
    @Size(min = 6, message = "{account.password.invalidSize}")
    @Column(name = "password")
    private String password;

    @Transient
    private String confirmPassword;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;


    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private User user;
}
