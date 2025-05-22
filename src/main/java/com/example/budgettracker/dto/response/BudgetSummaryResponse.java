package com.example.budgettracker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

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

    public BudgetSummaryResponse(Long budgetId, double totalIncome, double totalExpense) {
        this.budgetId = budgetId;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = totalIncome - totalExpense;
    }

    public Long getBudgetId() { return budgetId; }
    public double getTotalIncome() { return totalIncome; }
    public double getTotalExpense() { return totalExpense; }
    public double getBalance() { return balance; }
}
