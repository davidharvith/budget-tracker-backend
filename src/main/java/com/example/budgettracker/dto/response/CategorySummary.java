package com.example.budgettracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary of total amounts grouped by category.")
public record CategorySummary(
        @Schema(description = "The category name (e.g., Groceries, Utilities)", example = "Groceries")
        String category,

        @Schema(description = "Total amount for this category", example = "250.0")
        double total
) {}
