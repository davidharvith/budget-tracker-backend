package com.example.budgettracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Double amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type; // INCOME or EXPENSE

    private String description;

    @NotNull
    private LocalDateTime date = LocalDateTime.now();

    @Column(length = 64)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    public enum Type {
        INCOME, EXPENSE
    }

    // Constructors
    public Transaction() {}

    public Transaction(Double amount, Type type, String description,
                       LocalDateTime date,
                       String category, Budget budget) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.date = date != null ? date : LocalDateTime.now();
        this.category = category;
        this.budget = budget;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Budget getBudget() { return budget; }
    public void setBudget(Budget budget) { this.budget = budget; }
}
