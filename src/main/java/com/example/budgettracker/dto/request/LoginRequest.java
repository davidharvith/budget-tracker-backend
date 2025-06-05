package com.example.budgettracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO representing the request payload for user login.
 * Used in the authentication controller to capture credentials.
 */
@Schema(description = "Request body for user login.")
public record LoginRequest(

        // Username field with validation and documentation
        @Schema(description = "The user's username", example = "johndoe")
        @NotBlank(message = "Username is required")
        String username,

        // Password field with validation and documentation
        @Schema(description = "The user's password", example = "P@ssw0rd!")
        @NotBlank(message = "Password is required")
        String password

) {}
