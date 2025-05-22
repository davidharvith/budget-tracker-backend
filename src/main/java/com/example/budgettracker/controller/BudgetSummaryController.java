package com.example.budgettracker.controller;

import com.example.budgettracker.dto.response.BudgetSummaryResponse;
import com.example.budgettracker.service.BudgetSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Budget Summary", description = "Endpoint for retrieving a summary of a budget")
@RestController
@RequestMapping("/api/budgets/{budgetId}")
public class BudgetSummaryController {

    private final BudgetSummaryService budgetSummaryService;

    public BudgetSummaryController(BudgetSummaryService budgetSummaryService) {
        this.budgetSummaryService = budgetSummaryService;
    }

    @Operation(
            summary = "Get budget summary",
            description = "Returns total income, total expenses, and balance for the specified budget."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget summary returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    @GetMapping("/summary")
    public ResponseEntity<BudgetSummaryResponse> getBudgetSummary(
            @Parameter(description = "The budget ID", example = "1") @PathVariable Long budgetId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        BudgetSummaryResponse summary = budgetSummaryService.getBudgetSummary(budgetId, username);
        return ResponseEntity.ok(summary);
    }
}
