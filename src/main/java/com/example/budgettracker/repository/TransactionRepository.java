package com.example.budgettracker.repository;

import com.example.budgettracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for accessing and managing {@link Transaction} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard CRUD operations, and includes
 * custom queries to support budget-based analytics (e.g. totals by category and month).
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Retrieves all transactions linked to a specific budget.
     *
     * @param budgetId the ID of the budget
     * @return list of {@link Transaction} records belonging to the budget
     */
    List<Transaction> findByBudgetId(Long budgetId);

    /**
     * Calculates the total amount spent/earned grouped by category for a given budget and transaction type.
     *
     * @param budgetId the ID of the budget
     * @param type     the transaction type (INCOME or EXPENSE)
     * @return a list of Object arrays, where each entry is [category, totalAmount]
     */
    @Query("""
           SELECT t.category, SUM(t.amount) 
           FROM Transaction t 
           WHERE t.budget.id = :budgetId AND t.type = :type 
           GROUP BY t.category
           """)
    List<Object[]> sumByCategory(@Param("budgetId") Long budgetId, @Param("type") Transaction.Type type);

    /**
     * Calculates the total transaction amount per month, grouped by year and month.
     *
     * @param budgetId the ID of the budget
     * @param type     the transaction type (INCOME or EXPENSE)
     * @return a list of Object arrays: [year, month, totalAmount]
     */
    @Query("""
           SELECT EXTRACT(YEAR FROM t.date), EXTRACT(MONTH FROM t.date), SUM(t.amount) 
           FROM Transaction t 
           WHERE t.budget.id = :budgetId AND t.type = :type 
           GROUP BY EXTRACT(YEAR FROM t.date), EXTRACT(MONTH FROM t.date) 
           ORDER BY EXTRACT(YEAR FROM t.date), EXTRACT(MONTH FROM t.date)
           """)
    List<Object[]> sumByMonth(@Param("budgetId") Long budgetId, @Param("type") Transaction.Type type);
}
