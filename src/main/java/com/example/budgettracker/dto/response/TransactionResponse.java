package com.example.budgettracker.dto.response;

import com.example.budgettracker.model.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * DTO for returning transaction data to the client.
 * This class is used to decouple internal `Transaction` entities from what is exposed through the API.
 */
@Schema(description = "Response DTO representing a transaction record.")
public class TransactionResponse {

    @Schema(description = "Unique identifier of the transaction", example = "42")
    private Long id;

    @Schema(description = "Transaction amount (positive for income, negative for expense)", example = "100.0")
    private Double amount;

    @Schema(description = "Transaction type: INCOME or EXPENSE", example = "EXPENSE")
    private Transaction.Type type;

    @Schema(description = "Description or note for this transaction", example = "Dinner at a restaurant")
    private String description;

    @Schema(description = "Date and time of the transaction (ISO 8601 format)", example = "2025-05-22")
    private LocalDate date;

    /**
     * Default constructor for frameworks like Jackson.
     */
    public TransactionResponse() {}

    /**
     * Constructs a TransactionResponse from a Transaction entity.
     *
     * @param transaction the Transaction to convert
     */
    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.type = transaction.getType();
        this.description = transaction.getDescription();
        this.date = transaction.getDate();
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Transaction.Type getType() { return type; }
    public void setType(Transaction.Type type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
