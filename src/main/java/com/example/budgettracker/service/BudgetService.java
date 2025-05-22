package com.example.budgettracker.service;

import com.example.budgettracker.model.Budget;
import com.example.budgettracker.model.User;
import com.example.budgettracker.repository.BudgetRepository;
import com.example.budgettracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public BudgetService(BudgetRepository budgetRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    public Budget createBudget(String username, String name, Double amount) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        System.out.println("[SERVICE] Creating budget for user: " + user.getUsername() + ", id: " + user.getId());
        Budget budget = new Budget(name, amount, user);
        Budget saved = budgetRepository.save(budget);
        System.out.println("[SERVICE] Saved budget: " + saved + ", owner: " + saved.getOwner().getUsername());
        return saved;
    }


    public List<Budget> getBudgetsForUser(String username) {
        List<Budget> budgets = budgetRepository.findByOwnerUsername(username);
        System.out.println("[SERVICE] Budgets for user " + username + ": " + budgets);
        return budgets;
    }

    public Optional<Budget> getBudgetByIdAndUser(Long id, String username) {
        return budgetRepository.findById(id)
                .filter(budget -> budget.getOwner().getUsername().equals(username));
    }

    public Budget updateBudget(Long id, String username, String name, Double amount) {
        Budget budget = getBudgetByIdAndUser(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found or access denied"));
        budget.setName(name);
        budget.setAmount(amount);
        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long id, String username) {
        Budget budget = getBudgetByIdAndUser(id, username)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found or access denied"));
        budgetRepository.delete(budget);
    }
}
