package com.ou.journal.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ou.journal.configs.JwtService;
import com.ou.journal.enums.SecrectType;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenEmailFilter extends OncePerRequestFilter{

    @Autowired
    private JwtService jwtService;

    @Autowired
    @Qualifier("getUserDetail")
    private UserDetailsService userDetailsService;

    private void setAuthenticationContext(String token, HttpServletRequest request) {
        UserDetails account = getAccount(token);
        if (account != null) {

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(account,
                    null, account.getAuthorities());
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

    }

    // Re-create the user with given token
    private UserDetails getAccount(String token) {
        String userName = jwtService.getUserNameFromToken(token, SecrectType.EMAIL);
        Long id = jwtService.getIdFromToken(token, SecrectType.EMAIL);
        // Account account = new Account();
        System.out.println("[User ID] - " + id);
        System.out.println("[userName] - " + userName);
        System.out.println("[INFO] - Load the user detail");
        // account.setId(Integer.parseInt(claims[0]));
        // account.setuserName(claims[1]);
        return userDetailsService.loadUserByUsername(userName);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if(!request.getRequestURI().equals("/api/account/reviewer/verify")){
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("[DEBUG] - Start filter Token");
        System.out.println("[DEBUG] - uri=" + request.getRequestURI());

        String token = request.getParameter("token");
        if (!jwtService.isValidAccessToken(token, SecrectType.EMAIL)) {
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("[DEBUG] - Given token is valid");

        setAuthenticationContext(token, request);
        filterChain.doFilter(request, response);
    }
    
}
