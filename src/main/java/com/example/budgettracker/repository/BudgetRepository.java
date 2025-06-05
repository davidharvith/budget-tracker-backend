package com.example.budgettracker.repository;

import com.example.budgettracker.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for accessing and managing {@link Budget} entities.
 * This interface extends Spring Data JPA's {@link JpaRepository}, providing basic CRUD operations,
 * pagination, and sorting out of the box.
 */
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    /**
     * Retrieves all budgets that belong to a user with the specified username.
     *
     * @param username the username of the user who owns the budgets
     * @return a list of {@link Budget} entities associated with the given username
     */
    List<Budget> findByOwnerUsername(String username);
}
