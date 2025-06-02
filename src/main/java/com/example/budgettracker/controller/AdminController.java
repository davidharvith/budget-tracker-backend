package com.example.budgettracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin", description = "Endpoints accessible only by admin users")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

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

    //TODO Add more admin-only endpoints here in the future
}
