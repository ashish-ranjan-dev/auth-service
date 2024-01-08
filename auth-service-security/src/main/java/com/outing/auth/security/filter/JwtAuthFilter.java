package com.outing.auth.security.filter;

//import com.outing.auth.api.exception.AuthException;
import com.outing.commons.api.exception.OutingException;
import com.outing.auth.security.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    JwtUtils jwtUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtUtils.getToken(request);
            if (token != null && jwtUtils.validateToken(token)) {
                Authentication auth = new UsernamePasswordAuthenticationToken(jwtUtils.getUserFromToken(token), "", Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
            catch (OutingException ex) {
            SecurityContextHolder.clearContext();
            response.sendError(ex.getHttpStatus().value(), ex.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }
}
