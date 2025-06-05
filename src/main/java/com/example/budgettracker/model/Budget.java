package com.example.budgettracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a budget category for a user.
 * 
 * Each budget has a name, a target amount, an owner (User), and
 * a list of associated transactions.
 */
@Entity
@Table(name = "budgets")
public class Budget {

    /**
     * Primary key - auto-generated ID for the budget.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the budget (e.g., "Groceries", "Rent").
     */
    @NotBlank
    private String name;

    /**
     * Maximum allocated amount for this budget category.
     */
    @NotNull
    private Double amount;

    /**
     * The user who owns this budget.
     * 
     * Marked as @JsonIgnore to prevent infinite recursion when serializing.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User owner;

    /**
     * List of transactions tied to this budget.
     * 
     * Cascade type ALL ensures all related transactions are saved/removed with the budget.
     * orphanRemoval = true deletes transactions when removed from the list.
     */
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    // === Constructors ===

    public Budget() {
    }

    public Budget(String name, Double amount, User owner) {
        this.name = name;
        this.amount = amount;
        this.owner = owner;
    }

    // === Getters and Setters ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
