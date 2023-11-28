package com.ou.journal.components;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomRememberServices extends TokenBasedRememberMeServices {

    public CustomRememberServices(String key, UserDetailsService userDetailsService,
            RememberMeTokenAlgorithm encodingAlgorithm) {
        super(key, userDetailsService, encodingAlgorithm);
    }

    public CustomRememberServices(String key, UserDetailsService userDetailsService) {
        super(key, userDetailsService);
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
            HttpServletResponse response) {
        UserDetails userDetails = null;
        try {
            System.out.println("===========================================================");
            System.out.println("[DEBUG] - CALL BY REMEMBER ME");
            userDetails = super.processAutoLoginCookie(cookieTokens, request, response);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[DEBUG] - " + userDetails);
            throw new InvalidCookieException("Cookie is invalid");
        }
        
        return userDetails;
    }

}
