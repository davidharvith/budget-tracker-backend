package com.example.budgettracker.controller;

import com.example.budgettracker.model.Budget;
import com.example.budgettracker.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Budgets", description = "Endpoints for managing user budgets")
@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @Operation(
            summary = "Create a new budget",
            description = "Creates a new budget for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Budget created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<Budget> createBudget(
            @Valid @RequestBody BudgetRequest budgetRequest,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        Budget created = budgetService.createBudget(username, budgetRequest.name(), budgetRequest.amount());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Get all budgets",
            description = "Returns all budgets for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of budgets returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<Budget>> getBudgets(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        List<Budget> budgets = budgetService.getBudgetsForUser(username);
        return ResponseEntity.ok(budgets);
    }

    @Operation(
            summary = "Update a budget",
            description = "Updates the specified budget for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(
            @Parameter(description = "The budget ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody BudgetRequest budgetRequest,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        Budget updated = budgetService.updateBudget(id, username, budgetRequest.name(), budgetRequest.amount());
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete a budget",
            description = "Deletes the specified budget for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Budget deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @Parameter(description = "The budget ID", example = "1") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        budgetService.deleteBudget(id, username);
        return ResponseEntity.noContent().build();
    }

    // DTO for request validation
    @Schema(description = "Request body for creating or updating a budget")
    public record BudgetRequest(
            @Schema(description = "Name of the budget", example = "Vacation Fund")
            @NotBlank String name,

            @Schema(description = "Initial amount of the budget", example = "1500.0")
            @NotNull Double amount
    ) {}
}
