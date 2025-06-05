package com.example.budgettracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Represents a financial transaction linked to a specific budget.
 * Can be of type INCOME or EXPENSE.
 */
@Entity
@Table(name = "transactions",
            indexes = {
        @Index(name = "idx_budget_id", columnList = "budget_id"),
        @Index(name = "idx_transaction_date", columnList = "date"),
        @Index(name = "idx_budget_category", columnList = "budget_id, category")
    })
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The amount of the transaction (must be non-null).
     */
    @NotNull
    private Double amount;

    /**
     * Indicates whether the transaction is income or expense.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    /**
     * Optional description to explain the transaction.
     */
    private String description;

    /**
     * The date the transaction occurred. Defaults to today if not provided.
     */
    @NotNull
    private LocalDate date = LocalDate.now();

    /**
     * Optional category (e.g., "Groceries", "Rent").
     * Limited to 64 characters.
     */
    @Column(length = 64)
    private String category;

    /**
     * The budget to which this transaction belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    /**
     * Enum for transaction types.
     */
    public enum Type {
        INCOME, EXPENSE
    }

    // -------------------- Constructors --------------------

    /**
     * Default constructor for JPA.
     */
    public Transaction() {}

    /**
     * Full constructor to initialize all fields.
     *
     * @param amount      the transaction amount
     * @param type        transaction type (INCOME or EXPENSE)
     * @param description optional description
     * @param date        date of transaction (defaults to today if null)
     * @param category    optional category label
     * @param budget      associated budget
     */
    public Transaction(Double amount, Type type, String description,
                       LocalDate date, String category, Budget budget) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.date = date != null ? date : LocalDate.now();
        this.category = category;
        this.budget = budget;
    }

    // -------------------- Getters & Setters --------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Budget getBudget() { return budget; }
    public void setBudget(Budget budget) { this.budget = budget; }
}
