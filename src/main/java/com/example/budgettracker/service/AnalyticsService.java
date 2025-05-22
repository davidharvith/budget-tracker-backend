package com.example.budgettracker.service;

import com.example.budgettracker.dto.response.CategorySummary;
import com.example.budgettracker.dto.response.MonthlySummary;
import com.example.budgettracker.model.Transaction;
import com.example.budgettracker.repository.BudgetRepository;
import com.example.budgettracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public AnalyticsService(TransactionRepository transactionRepository, BudgetRepository budgetRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    // Analytics by Category
    public List<CategorySummary> sumByCategory(Long budgetId, Transaction.Type type, String username) {
        // Optional: Check budget ownership
        budgetRepository.findById(budgetId)
                .filter(b -> b.getOwner().getUsername().equals(username))
                .orElseThrow(() -> new IllegalArgumentException("Budget not found or access denied"));

        List<Object[]> results = transactionRepository.sumByCategory(budgetId, type);
        return results.stream()
                .map(obj -> new CategorySummary((String) obj[0], ((Number) obj[1]).doubleValue()))
                .collect(Collectors.toList());
    }

    // Analytics by Month
    public List<MonthlySummary> sumByMonth(Long budgetId, Transaction.Type type, String username) {
        budgetRepository.findById(budgetId)
                .filter(b -> b.getOwner().getUsername().equals(username))
                .orElseThrow(() -> new IllegalArgumentException("Budget not found or access denied"));

        List<Object[]> results = transactionRepository.sumByMonth(budgetId, type);
        // In sumByMonth method
        return results.stream()
                .map(obj -> new MonthlySummary(
                        (obj[0] != null) ? ((Number) obj[0]).intValue() : 0, // year
                        (obj[1] != null) ? ((Number) obj[1]).intValue() : 0, // month
                        (obj[2] != null) ? ((Number) obj[2]).doubleValue() : 0.0 // total
                ))
                .collect(Collectors.toList());

    }
}
