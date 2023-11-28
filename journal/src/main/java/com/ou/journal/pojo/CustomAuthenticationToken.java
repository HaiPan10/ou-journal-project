package com.ou.journal.pojo;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken{

    private String roleName;

    public CustomAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String roleName) {
        super(principal, credentials, authorities);
        this.roleName = roleName;
    }

    public CustomAuthenticationToken(Object principal, Object credentials, String roleName) {
        super(principal, credentials);
        this.roleName = roleName;
    }
    
}
