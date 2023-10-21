package com.ou.journal.filter;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ClientAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response)
            throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        String username = obtainUsername(request);
        username = username != null ? username.trim() : "";
        String password = obtainPassword(request);
        password = password != null ? password.trim() : "";
        String roleName = obtainRoleName(request);
        roleName = roleName != null ? roleName.trim() : "";
        username = String.format("%s%s%s", username,
                ",", roleName);
        UsernamePasswordAuthenticationToken authenticationToken = 
            UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        setDetails(request, authenticationToken);
        return this.getAuthenticationManager()
                .authenticate(authenticationToken);
    }

    private String obtainRoleName(HttpServletRequest request) {
        return request.getParameter("role");
    }
}
