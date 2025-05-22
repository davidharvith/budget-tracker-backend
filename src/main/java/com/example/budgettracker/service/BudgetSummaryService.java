package com.example.budgettracker.service;

import com.example.budgettracker.dto.response.BudgetSummaryResponse;
import com.example.budgettracker.model.Transaction;
import com.example.budgettracker.model.Transaction.Type;
import com.example.budgettracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetSummaryService {
    private final TransactionRepository transactionRepository;

    public BudgetSummaryService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public BudgetSummaryResponse getBudgetSummary(Long budgetId, String username) {
        List<Transaction> transactions = transactionRepository.findByBudgetId(budgetId);
        double totalIncome = transactions.stream()
                .filter(t -> t.getType() == Type.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
        double totalExpense = transactions.stream()
                .filter(t -> t.getType() == Type.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
        return new BudgetSummaryResponse(budgetId, totalIncome, totalExpense);
    }
}
