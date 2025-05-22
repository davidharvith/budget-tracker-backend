package com.example.budgettracker.repository;

import com.example.budgettracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByBudgetId(Long budgetId);

    // --- NEW: Sum by Category ---
    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t " +
            "WHERE t.budget.id = :budgetId AND t.type = :type " +
            "GROUP BY t.category")
    List<Object[]> sumByCategory(@Param("budgetId") Long budgetId, @Param("type") Transaction.Type type);

    // --- NEW: Sum by Month ---
    @Query("SELECT EXTRACT(YEAR FROM t.date), EXTRACT(MONTH FROM t.date), SUM(t.amount) FROM Transaction t " +
            "WHERE t.budget.id = :budgetId AND t.type = :type " +
            "GROUP BY EXTRACT(YEAR FROM t.date), EXTRACT(MONTH FROM t.date) " +
            "ORDER BY EXTRACT(YEAR FROM t.date), EXTRACT(MONTH FROM t.date)")
    List<Object[]> sumByMonth(@Param("budgetId") Long budgetId, @Param("type") Transaction.Type type);

}
