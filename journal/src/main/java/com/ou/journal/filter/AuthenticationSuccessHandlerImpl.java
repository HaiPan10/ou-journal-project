package com.ou.journal.filter;

import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private String targetEndpoint;

    public AuthenticationSuccessHandlerImpl(String targetEndpoint){
        this.targetEndpoint = targetEndpoint;
    }

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        redirectStrategy.sendRedirect(request, response, targetEndpoint);
    }

}
