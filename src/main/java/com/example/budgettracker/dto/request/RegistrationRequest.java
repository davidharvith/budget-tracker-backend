package com.example.budgettracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request body for user registration.")
public record RegistrationRequest(
        @Schema(description = "Unique username for the new user", example = "john_doe")
        @NotBlank(message = "Username is required")
        String username,

        @Schema(description = "User's email address", example = "john@example.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "User's password (min 8 characters)", example = "password123")
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {}
