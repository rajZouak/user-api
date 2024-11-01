package com.example.userapi.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.userapi.model.User;
import com.example.userapi.service.UserService;
import com.example.userapi.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;

    public JwtTokenFilter(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer")) {
            token = token.substring(7);

            try {
                // Verifying user token
                DecodedJWT decodedJWT = JwtUtil.verifyToken(token);
                String email = decodedJWT.getSubject();
                User user = userService.getUserDetailsByEmail(email);

                // Creating an authentication token
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (Exception e) {
                System.out.println("Token validation failed: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
