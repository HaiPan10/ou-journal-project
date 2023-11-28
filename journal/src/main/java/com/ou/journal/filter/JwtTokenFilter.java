package com.ou.journal.filter;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ou.journal.configs.JwtService;
import com.ou.journal.enums.SecrectType;
import com.ou.journal.service.interfaces.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    private void setAuthenticationContext(String token, HttpServletRequest request)
            throws UsernameNotFoundException, Exception {
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
    private UserDetails getAccount(String token) throws UsernameNotFoundException, Exception {
        String userName = jwtService.getUserNameFromToken(token, SecrectType.DEFAULT);
        String roleName = jwtService.getRoleNameFromToken(token, SecrectType.DEFAULT);
        return userDetailsService.loadUserByUsernameAndRoleName(userName, roleName);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().equals("/api/accounts/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = jwtService.getAuthorization(request);
        request.setCharacterEncoding("UTF-8");

        if (header == null || !header.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("[DEBUG] - Start filter Token");
        System.out.println("[DEBUG] - uri=" + request.getRequestURI());
        System.out.println("[DEBUG] - Has Authorization Bearer");

        String token = jwtService.getAccessToken(header);

        if (!jwtService.isValidAccessToken(token, SecrectType.DEFAULT)) {
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("[DEBUG] - Given token is valid");

        try {
            setAuthenticationContext(token, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        filterChain.doFilter(request, response);
    }
}
