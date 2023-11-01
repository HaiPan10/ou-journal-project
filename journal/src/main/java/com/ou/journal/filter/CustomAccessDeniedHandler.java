package com.ou.journal.filter;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
            throws IOException, ServletException {
                System.out.println("[DEBUG] - INSIDE THE ERROR PAGE");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("text/html");
        // request.getRequestDispatcher("/error/403").forward(request, response);
        // response.getWriter().write("Access Denied!");
    }

}
