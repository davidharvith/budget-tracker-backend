package com.example.budgettracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary of total amounts grouped by year and month.")
public record MonthlySummary(
        @Schema(description = "The year for this summary", example = "2025")
        int year,

        @Schema(description = "The month for this summary (1 = January, 12 = December)", example = "5")
        int month,

        @Schema(description = "Total amount for this year and month", example = "300.0")
        double total
) {}
