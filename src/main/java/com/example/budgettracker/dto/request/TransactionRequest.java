package com.example.budgettracker.dto.request;

import com.example.budgettracker.model.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Request body for creating or updating a transaction.")
public class TransactionRequest {

    @Schema(description = "Transaction amount (positive for income, negative for expense)", example = "120.50", required = true)
    @NotNull
    private Double amount;

    @Schema(description = "Transaction type: INCOME or EXPENSE", example = "EXPENSE", required = true)
    @NotNull
    private Transaction.Type type;

    @Schema(description = "Description or note for this transaction", example = "Lunch at cafe", maxLength = 255)
    @Size(max = 255)
    private String description;

    @Schema(description = "Date and time of the transaction (ISO 8601 format)", example = "2025-05-22T13:45:00")
    private LocalDateTime date;

    @Schema(description = "Category for this transaction", example = "Groceries")
    private String category;

    // Getters and setters

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Transaction.Type getType() { return type; }
    public void setType(Transaction.Type type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
