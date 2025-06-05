package com.example.budgettracker.service;

import com.example.budgettracker.model.Budget;
import com.example.budgettracker.model.Transaction;
import com.example.budgettracker.repository.BudgetRepository;
import com.example.budgettracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class handling all business logic related to transactions.
 * Ensures user-level access control for all operations.
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public TransactionService(TransactionRepository transactionRepository, BudgetRepository budgetRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    /**
     * Adds a new transaction to a specific budget.
     *
     * @param budgetId    the ID of the budget to add the transaction to
     * @param transaction the transaction to be added
     * @param username    the username of the user performing the operation
     * @return the saved transaction
     * @throws IllegalArgumentException if the budget doesn't exist or user doesn't own it
     */
    public Transaction addTransaction(Long budgetId, Transaction transaction, String username) {
        Budget budget = budgetRepository.findById(budgetId)
                .filter(b -> b.getOwner().getUsername().equals(username))
                .orElseThrow(() -> new IllegalArgumentException("Budget not found or access denied"));

        transaction.setBudget(budget);
        return transactionRepository.save(transaction);
    }

    /**
     * Retrieves all transactions for a given budget.
     *
     * @param budgetId the budget's ID
     * @param username the user requesting the data
     * @return list of transactions
     * @throws IllegalArgumentException if the budget doesn't exist or access is denied
     */
    public List<Transaction> getTransactions(Long budgetId, String username) {
        budgetRepository.findById(budgetId)
                .filter(b -> b.getOwner().getUsername().equals(username))
                .orElseThrow(() -> new IllegalArgumentException("Budget not found or access denied"));

        return transactionRepository.findByBudgetId(budgetId);
    }

    /**
     * Updates an existing transaction.
     *
     * @param transactionId     the ID of the transaction to update
     * @param updatedTransaction the transaction data to apply
     * @param username          the user performing the update
     * @return the updated transaction
     * @throws IllegalArgumentException if transaction not found or user not authorized
     */
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

    /**
     * Deletes a transaction if owned by the requesting user.
     *
     * @param transactionId the ID of the transaction to delete
     * @param username      the user requesting deletion
     * @throws IllegalArgumentException if transaction not found or user not authorized
     */
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
