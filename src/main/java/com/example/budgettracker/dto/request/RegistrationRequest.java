package com.example.budgettracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * DTO representing the request payload for user registration.
 * This is used to receive new user details from the frontend.
 */
@Schema(description = "Request body for user registration.")
public record RegistrationRequest(

        // Username for the new user, must not be blank.
        @Schema(description = "Unique username for the new user", example = "john_doe")
        @NotBlank(message = "Username is required")
        String username,

        // Email address, must not be blank and must be in a valid email format.
        @Schema(description = "User's email address", example = "john@example.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        // Password must be at least 8 characters long and not blank.
        @Schema(description = "User's password (min 8 characters)", example = "password123")
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password

) {}
