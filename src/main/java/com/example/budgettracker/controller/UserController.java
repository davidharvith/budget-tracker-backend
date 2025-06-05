package com.example.budgettracker.controller;

import com.example.budgettracker.dto.request.RegistrationRequest;
import com.example.budgettracker.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.security.core.Authentication;

import java.util.Map;

@Tag(name = "User", description = "Operations related to user accounts")
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // Constructor injection of the UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint for user registration.
     * Accepts a RegistrationRequest payload and creates a new user in the system.
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with a unique username and email."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate user")
    })
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationRequest request) {
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }

    /**
     * Returns the currently authenticated user's username.
     * This is useful for client-side apps to confirm the login status or personalize the UI.
     */
    @Operation(
            summary = "Get current user info",
            description = "Returns the username of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current user info returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @Parameter(hidden = true) Authentication authentication
    ) {
        // Uses Spring Security's Authentication object to get the username
        return ResponseEntity.ok(Map.of("username", authentication.getName()));
    }
}
