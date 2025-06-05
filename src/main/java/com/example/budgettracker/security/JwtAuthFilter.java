package com.example.budgettracker.security;

import com.example.budgettracker.service.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;

/**
 * Filter that processes incoming requests to validate JWT tokens.
 * If the token is valid, it sets up the Spring Security context with the authenticated user.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Filters each request to check for a valid JWT token.
     * If the token is present and valid, it sets the authentication context.
     *
     * @param request     incoming HTTP request
     * @param response    HTTP response
     * @param filterChain chain of filters to continue the request
     * @throws ServletException in case of servlet errors
     * @throws IOException      in case of I/O errors
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Allow preflight (CORS) requests to pass through
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtUtil.getUsernameFromToken(token);

                if (!jwtUtil.validateToken(token)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                    return;
                }

                // Load user details from DB
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                // Extract roles from token
                List<String> roles = jwtUtil.getRolesFromToken(token);
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                // Build and set authentication object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (JwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Skips filtering for endpoints that don't require authentication.
     * Used for public endpoints like registration and login.
     *
     * @param request incoming request
     * @return true if the filter should be skipped
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/register") || path.equals("/api/login");
    }
}
