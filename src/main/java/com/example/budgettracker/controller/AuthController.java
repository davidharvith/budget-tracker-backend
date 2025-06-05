package com.example.budgettracker.controller;

import com.example.budgettracker.dto.request.LoginRequest;
import com.example.budgettracker.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Handles authentication-related endpoints, such as login.
 */
@Tag(name = "Authentication", description = "Endpoints for user authentication (login)")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param loginRequest The user's login credentials.
     * @return A JWT token if authentication is successful, or a 401 response if not.
     */
    @Operation(
            summary = "User login",
            description = "Authenticates a user and returns a JWT token if successful."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            String token = jwtUtil.generateToken(
                    authentication.getName(),
                    authentication.getAuthorities()
            );

            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of("message", "Invalid credentials"));
        }
    }
}
