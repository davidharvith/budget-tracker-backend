package com.example.budgettracker.service;

import com.example.budgettracker.model.Budget;
import com.example.budgettracker.model.User;
import com.example.budgettracker.repository.BudgetRepository;
import com.example.budgettracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing user budgets.
 * Handles creation, retrieval, update, and deletion of budgets.
 */
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public BudgetService(BudgetRepository budgetRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new budget for the given user.
     *
     * @param username the username of the budget owner
     * @param name the name of the budget
     * @param amount the starting budget amount
     * @return the saved Budget entity
     */
    public Budget createBudget(String username, String name, Double amount) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Budget budget = new Budget(name, amount, user);
        return budgetRepository.save(budget);
    }

    /**
     * Retrieves all budgets for a specific user.
     *
     * @param username the username of the owner
     * @return a list of budgets belonging to the user
     */
    public List<Budget> getBudgetsForUser(String username) {
        return budgetRepository.findByOwnerUsername(username);
    }

    /**
     * Retrieves a budget by its ID and checks if it belongs to the given user.
     *
     * @param id the budget ID
     * @param username the username of the requesting user
     * @return an Optional containing the budget if it exists and is owned by the user
     */
    public Optional<Budget> getBudgetByIdAndUser(Long id, String username) {
        return budgetRepository.findById(id)
                .filter(budget -> budget.getOwner().getUsername().equals(username));
    }

    /**
     * Updates an existing budget if it belongs to the user.
     *
     * @param id the ID of the budget to update
     * @param username the username of the owner
     * @param name the new name of the budget
     * @param amount the new amount of the budget
     * @return the updated Budget entity
     */
    public Budget updateBudget(Long id, String username, String name, Double amount) {
        Budget budget = getBudgetByIdAndUser(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found or access denied"));
        budget.setName(name);
        budget.setAmount(amount);
        return budgetRepository.save(budget);
    }

    /**
     * Deletes a budget if it exists and is owned by the given user.
     *
     * @param id the ID of the budget to delete
     * @param username the username of the owner
     */
    public void deleteBudget(Long id, String username) {
        Budget budget = getBudgetByIdAndUser(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found or access denied"));
        budgetRepository.delete(budget);
    }
}
