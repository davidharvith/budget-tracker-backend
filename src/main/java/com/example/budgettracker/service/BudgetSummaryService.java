package com.example.budgettracker.service;

import com.example.budgettracker.dto.response.BudgetSummaryResponse;
import com.example.budgettracker.model.Budget;
import com.example.budgettracker.model.Transaction;
import com.example.budgettracker.model.Transaction.Type;
import com.example.budgettracker.repository.BudgetRepository;
import com.example.budgettracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for calculating budget summaries including total income, expenses, and remaining balance.
 */
@Service
public class BudgetSummaryService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public BudgetSummaryService(TransactionRepository transactionRepository,
                                 BudgetRepository budgetRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    /**
     * Computes the summary of a budget: total income, total expenses, and remaining balance.
     *
     * @param budgetId ID of the budget to summarize
     * @param username Username requesting the summary (used for future access control)
     * @return BudgetSummaryResponse containing the financial summary
     */
    public BudgetSummaryResponse getBudgetSummary(Long budgetId, String username) {
        // Get all transactions for the given budget
        List<Transaction> transactions = transactionRepository.findByBudgetId(budgetId);

        // Calculate total income
        double totalIncome = transactions.stream()
                .filter(t -> t.getType() == Type.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        // Calculate total expenses
        double totalExpense = transactions.stream()
                .filter(t -> t.getType() == Type.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        // Retrieve original budget amount (defaults to 0.0 if not found)
        double budgetAmount = budgetRepository.findById(budgetId)
                .map(Budget::getAmount)
                .orElse(0.0);

        // Calculate balance: starting budget + income - expenses
        double balance = budgetAmount + totalIncome - totalExpense;

        return new BudgetSummaryResponse(budgetId, totalIncome, totalExpense, balance);
    }
}
