package com.example.budgettracker.service;

import com.example.budgettracker.dto.response.CategorySummary;
import com.example.budgettracker.dto.response.MonthlySummary;
import com.example.budgettracker.model.Transaction;
import com.example.budgettracker.repository.BudgetRepository;
import com.example.budgettracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for providing analytical summaries over transactions.
 * Includes methods for aggregating transaction amounts by category and by month.
 */
@Service
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public AnalyticsService(TransactionRepository transactionRepository, BudgetRepository budgetRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    /**
     * Returns the total amount of transactions grouped by category for a given budget and transaction type.
     *
     * @param budgetId the ID of the budget
     * @param type     the type of transactions (INCOME or EXPENSE)
     * @param username the username of the budget owner
     * @return a list of CategorySummary DTOs representing category totals
     */
    public List<CategorySummary> sumByCategory(Long budgetId, Transaction.Type type, String username) {
        budgetRepository.findById(budgetId)
                .filter(b -> b.getOwner().getUsername().equals(username))
                .orElseThrow(() -> new IllegalArgumentException("Budget not found or access denied"));

        List<Object[]> results = transactionRepository.sumByCategory(budgetId, type);
        return results.stream()
                .map(obj -> new CategorySummary((String) obj[0], ((Number) obj[1]).doubleValue()))
                .collect(Collectors.toList());
    }

    /**
     * Returns the total amount of transactions grouped by year and month for a given budget and transaction type.
     *
     * @param budgetId the ID of the budget
     * @param type     the type of transactions (INCOME or EXPENSE)
     * @param username the username of the budget owner
     * @return a list of MonthlySummary DTOs representing monthly totals
     */
    public List<MonthlySummary> sumByMonth(Long budgetId, Transaction.Type type, String username) {
        budgetRepository.findById(budgetId)
                .filter(b -> b.getOwner().getUsername().equals(username))
                .orElseThrow(() -> new IllegalArgumentException("Budget not found or access denied"));

        List<Object[]> results = transactionRepository.sumByMonth(budgetId, type);
        return results.stream()
                .map(obj -> new MonthlySummary(
                        (obj[0] != null) ? ((Number) obj[0]).intValue() : 0, // year
                        (obj[1] != null) ? ((Number) obj[1]).intValue() : 0, // month
                        (obj[2] != null) ? ((Number) obj[2]).doubleValue() : 0.0 // total
                ))
                .collect(Collectors.toList());
    }
}
