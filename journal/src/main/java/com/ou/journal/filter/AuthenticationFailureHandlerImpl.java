package com.ou.journal.filter;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

    private String targetEndpoint;

    public AuthenticationFailureHandlerImpl(String targetEndpoint){
        this.targetEndpoint = targetEndpoint;
    }

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        redirectStrategy.sendRedirect(request, response, targetEndpoint);
    }

}
