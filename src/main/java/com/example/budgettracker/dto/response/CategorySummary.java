package com.example.budgettracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO representing the total amount of transactions grouped by category.
 * 
 * This is commonly used in analytics or summary views, such as:
 * - Pie charts or category breakdowns
 * - /api/budgets/{id}/category-summary-type endpoints (if implemented)
 */
@Schema(description = "Summary of total amounts grouped by category.")
public record CategorySummary(

        @Schema(description = "The category name (e.g., Groceries, Utilities)", example = "Groceries")
        String category,

        @Schema(description = "Total amount for this category", example = "250.0")
        double total
) {}
