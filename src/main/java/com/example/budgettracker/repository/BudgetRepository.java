package com.example.budgettracker.repository;

import com.example.budgettracker.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByOwnerUsername(String username);
}
