package com.example.budgettracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin-only endpoints for restricted functionality.
 * All methods in this controller are secured for users with the ADMIN role.
 */
@Tag(name = "Admin", description = "Endpoints accessible only by admin users")
/*************  ✨ Windsurf Command ⭐  *************/
    /**
     * Endpoint that can only be accessed by users with the ADMIN role.
     * If the user has the ADMIN role, a confirmation message is returned.
     * Otherwise, a 403 Forbidden response is returned.
     */
/*******  6b66678e-3b60-4c0c-96c9-2cf56cd058e7  *******/@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /**
     * Test endpoint to verify admin access.
     *
     * @return String confirmation if user has ADMIN role
     */
    @Operation(
            summary = "Admin-only test endpoint",
            description = "Returns a confirmation message if the user has the ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You are an admin!"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not an admin")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/only")
    public String adminOnlyEndpoint() {
        return "You are an admin!";
    }

    // TODO: Add more admin-only endpoints here in the future
}
