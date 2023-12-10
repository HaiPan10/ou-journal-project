package com.ou.journal.components;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import com.ou.journal.pojo.AuthenticationUser;
import com.ou.journal.pojo.UserRole;
import com.ou.journal.service.interfaces.UserRoleService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomRememberServices extends TokenBasedRememberMeServices {

    private UserRoleService userRoleService;

    public CustomRememberServices(String key, UserDetailsService userDetailsService,
            RememberMeTokenAlgorithm encodingAlgorithm, UserRoleService userRoleService) {
        super(key, userDetailsService, encodingAlgorithm);
        this.userRoleService = userRoleService;
    }

    public CustomRememberServices(String key, UserDetailsService userDetailsService, UserRoleService userRoleService) {
        super(key, userDetailsService);
        this.userRoleService = userRoleService;
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
            HttpServletResponse response) {
        AuthenticationUser userDetails = (AuthenticationUser) super.processAutoLoginCookie(cookieTokens, request,
                response);
        if (userDetails != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("ROLE")) {
                    String cookieRoleName = cookie.getValue();
                    Optional<UserRole> userRole = userRoleService.getByUserNameAndRoleName(userDetails.getUsername(), cookieRoleName);
                    if (userRole.isPresent()) {
                        // Because of the userDetails authorities is immutable
                        // so we need to create a new AuthenticationUser and return it
                        // with the correct roleName
                        String roleName = userRole.get().getRole().getRoleName();
                        Set<GrantedAuthority> authorities = new HashSet<>();
                        authorities.add(new SimpleGrantedAuthority(roleName));
                        AuthenticationUser user = new AuthenticationUser(userDetails.getUsername(), userDetails.getPassword(), authorities);
                        user.setEmail(userDetails.getEmail());
                        user.setFirstName(userDetails.getFirstName());
                        user.setLastName(userDetails.getLastName());
                        user.setId(userDetails.getId());
                        user.setRoleName(roleName);
                        return user;
                    } else {
                        throw new InvalidCookieException("Cookie is invalid");
                    }
                }
            }

        }

        throw new InvalidCookieException("Cookie is invalid");
    }

}
