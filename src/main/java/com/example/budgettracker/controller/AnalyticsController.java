package com.example.budgettracker.controller;

import com.example.budgettracker.dto.response.CategorySummary;
import com.example.budgettracker.dto.response.MonthlySummary;
import com.example.budgettracker.model.Transaction;
import com.example.budgettracker.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for analytics endpoints related to transactions in a budget.
 */
@Tag(name = "Analytics", description = "Endpoints for analytics by category and by month for a budget")
@RestController
@RequestMapping("/api/budgets/{budgetId}/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Get total transaction amounts grouped by category.
     *
     * @param budgetId   ID of the budget
     * @param type       Transaction type (INCOME or EXPENSE)
     * @param userDetails Authenticated user details
     * @return List of CategorySummary objects
     */
    @Operation(
            summary = "Get analytics by category",
            description = "Returns total amounts grouped by category for the specified budget and transaction type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analytics by category returned successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not have access to this budget"),
            @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    @GetMapping("/category")
    public ResponseEntity<List<CategorySummary>> getCategoryAnalytics(
            @Parameter(description = "The budget ID", example = "1") @PathVariable Long budgetId,
            @Parameter(description = "Transaction type (INCOME or EXPENSE)", example = "EXPENSE") @RequestParam Transaction.Type type,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        List<CategorySummary> summaries = analyticsService.sumByCategory(budgetId, type, username);
        return ResponseEntity.ok(summaries);
    }

    /**
     * Get total transaction amounts grouped by year and month.
     *
     * @param budgetId    ID of the budget
     * @param type        Transaction type (INCOME or EXPENSE)
     * @param userDetails Authenticated user details
     * @return List of MonthlySummary objects
     */
    @Operation(
            summary = "Get analytics by month",
            description = "Returns total amounts grouped by year and month for the specified budget and transaction type."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analytics by month returned successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not have access to this budget"),
            @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    @GetMapping("/month")
    public ResponseEntity<List<MonthlySummary>> getMonthlyAnalytics(
            @Parameter(description = "The budget ID", example = "1") @PathVariable Long budgetId,
            @Parameter(description = "Transaction type (INCOME or EXPENSE)", example = "EXPENSE") @RequestParam Transaction.Type type,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        List<MonthlySummary> summaries = analyticsService.sumByMonth(budgetId, type, username);
        return ResponseEntity.ok(summaries);
    }
}
