package com.example.budgettracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO used to return a high-level summary of a specific budget.
 * This is returned from the GET /api/budgets/{budgetId}/summary endpoint.
 */
@Schema(description = "Summary of a budget, including total income, expenses, and balance.")
public class BudgetSummaryResponse {

    @Schema(description = "The unique identifier of the budget", example = "1")
    private Long budgetId;

    @Schema(description = "Total income for this budget", example = "1200.00")
    private double totalIncome;

    @Schema(description = "Total expenses for this budget", example = "800.00")
    private double totalExpense;

    @Schema(description = "Remaining balance (income minus expenses)", example = "400.00")
    private double balance;

    /**
     * Constructor for BudgetSummaryResponse.
     * Typically called by the service layer when preparing a response.
     *
     * @param budgetId the ID of the budget
     * @param totalIncome sum of all income transactions
     * @param totalExpense sum of all expense transactions
     * @param balance totalIncome - totalExpense
     */
    public BudgetSummaryResponse(Long budgetId, double totalIncome, double totalExpense, double balance) {
        this.budgetId = budgetId;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = balance;
    }

    // === Getters (used by Spring to serialize to JSON) ===

    public Long getBudgetId() {
        return budgetId;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public double getBalance() {
        return balance;
    }
}
