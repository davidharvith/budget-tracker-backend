package com.example.budgettracker.config;

import com.example.budgettracker.security.JwtAuthFilter;
import com.example.budgettracker.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration for the Budget Tracker application.
 * 
 * - Enables JWT-based stateless authentication.
 * - Defines public endpoints like login/register and Swagger docs.
 * - Applies CORS settings for local frontend integration.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtAuthFilter jwtAuthFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Defines the main security filter chain.
     * 
     * - Disables CSRF (because it's a stateless REST API)
     * - Enables CORS
     * - Sets session management to stateless
     * - Whitelists public endpoints (auth + Swagger UI)
     * - Secures all other endpoints
     * - Adds JWT filter before default Spring Auth filter
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Needed for CORS preflight
                .requestMatchers(
                    "/", "/favicon.ico", "/api/register", "/api/login",
                    "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**"
                ).permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures the authentication manager with custom user service and password encoder.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder());
        return auth.build();
    }

    /**
     * Bean for encoding passwords using BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines CORS policy for frontend requests.
     * 
     * Allows requests from local frontend dev server.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        System.out.println("CORS config loaded");

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:5173")); // Vite dev server
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
