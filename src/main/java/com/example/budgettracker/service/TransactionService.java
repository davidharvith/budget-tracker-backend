package com.example.budgettracker.service;

import com.example.budgettracker.model.Budget;
import com.example.budgettracker.model.Transaction;
import com.example.budgettracker.repository.BudgetRepository;
import com.example.budgettracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public TransactionService(TransactionRepository transactionRepository, BudgetRepository budgetRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    public Transaction addTransaction(Long budgetId, Transaction transaction, String username) {
        Budget budget = budgetRepository.findById(budgetId)
                .filter(b -> b.getOwner().getUsername().equals(username))
                .orElseThrow(() -> new IllegalArgumentException("Budget not found or access denied"));
        transaction.setBudget(budget);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactions(Long budgetId, String username) {
        Budget budget = budgetRepository.findById(budgetId)
                .filter(b -> b.getOwner().getUsername().equals(username))
                .orElseThrow(() -> new IllegalArgumentException("Budget not found or access denied"));
        return transactionRepository.findByBudgetId(budgetId);
    }

    public Transaction updateTransaction(Long transactionId, Transaction updatedTransaction, String username) {
        Transaction existing = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        Budget budget = existing.getBudget();
        if (!budget.getOwner().getUsername().equals(username)) {
            throw new IllegalArgumentException("Access denied");
        }
        existing.setAmount(updatedTransaction.getAmount());
        existing.setType(updatedTransaction.getType());
        existing.setDescription(updatedTransaction.getDescription());
        existing.setDate(updatedTransaction.getDate());
        return transactionRepository.save(existing);
    }

    public void deleteTransaction(Long transactionId, String username) {
        Transaction existing = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        Budget budget = existing.getBudget();
        if (!budget.getOwner().getUsername().equals(username)) {
            throw new IllegalArgumentException("Access denied");
        }
        transactionRepository.delete(existing);
    }
}
