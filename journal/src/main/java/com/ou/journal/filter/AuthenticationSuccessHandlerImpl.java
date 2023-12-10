package com.ou.journal.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.ou.journal.pojo.AuthenticationUser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PropertySource("classpath:configs.properties")
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private String targetEndpoint;
    @Value("${COOKIE_MAX_AGE}")
    private int cookieMaxAge = 0;

    public AuthenticationSuccessHandlerImpl(String targetEndpoint) {
        this.targetEndpoint = targetEndpoint;
    }

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        AuthenticationUser authenticationUser = (AuthenticationUser) authentication.getPrincipal();
        Cookie cookie = new Cookie("ROLE", authenticationUser.getRoleName());
        cookie.setMaxAge(cookieMaxAge);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        redirectStrategy.sendRedirect(request, response, targetEndpoint);
    }

}
