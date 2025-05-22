package com.example.budgettracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for user login.")
public record LoginRequest(
        @Schema(description = "The user's username", example = "johndoe")
        @NotBlank(message = "Username is required")
        String username,

        @Schema(description = "The user's password", example = "P@ssw0rd!")
        @NotBlank(message = "Password is required")
        String password
) {}
