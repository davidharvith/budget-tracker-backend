package com.example.budgettracker.controller;

import com.example.budgettracker.model.Transaction;
import com.example.budgettracker.service.TransactionService;
import com.example.budgettracker.dto.request.TransactionRequest;
import com.example.budgettracker.dto.response.TransactionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Transactions", description = "Endpoints for managing transactions within a budget")
@RestController
@RequestMapping("/api/budgets/{budgetId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    // Constructor injection of service layer
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Adds a new transaction (income or expense) to a specific budget.
     */
    @Operation(
            summary = "Add a new transaction",
            description = "Creates a new transaction for the specified budget."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    @PostMapping
    public ResponseEntity<TransactionResponse> addTransaction(
            @Parameter(description = "The budget ID", example = "1") @PathVariable Long budgetId,
            @Valid @RequestBody TransactionRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();

        // Construct a transaction object from the incoming request
        Transaction transaction = new Transaction(
                request.getAmount(),
                request.getType(),
                request.getDescription(),
                request.getDate(),
                request.getCategory(),
                null // Budget is set in the service layer
        );

        // Delegate creation to service
        Transaction created = transactionService.addTransaction(budgetId, transaction, username);

        // Return wrapped response with 201 status
        return ResponseEntity.status(HttpStatus.CREATED).body(new TransactionResponse(created));
    }

    /**
     * Fetches all transactions for the given budget and authenticated user.
     */
    @Operation(
            summary = "Get all transactions for a budget",
            description = "Returns all transactions for the specified budget."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of transactions returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @Parameter(description = "The budget ID", example = "1") @PathVariable Long budgetId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();

        // Get raw transactions from service
        List<Transaction> transactions = transactionService.getTransactions(budgetId, username);

        // Map to response DTOs
        List<TransactionResponse> responses = transactions.stream()
                .map(TransactionResponse::new)
                .toList();

        return ResponseEntity.ok(responses);
    }

    /**
     * Updates a specific transaction for a budget.
     */
    @Operation(
            summary = "Update a transaction",
            description = "Updates the specified transaction for the budget."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @Parameter(description = "The budget ID", example = "1") @PathVariable Long budgetId,
            @Parameter(description = "The transaction ID", example = "10") @PathVariable Long transactionId,
            @Valid @RequestBody TransactionRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();

        // Construct updated transaction (budget will be reassigned in service)
        Transaction transaction = new Transaction(
                request.getAmount(),
                request.getType(),
                request.getDescription(),
                request.getDate(),
                request.getCategory(),
                null
        );

        // Delegate update
        Transaction updated = transactionService.updateTransaction(transactionId, transaction, username);

        return ResponseEntity.ok(new TransactionResponse(updated));
    }

    /**
     * Deletes a transaction from the specified budget.
     */
    @Operation(
            summary = "Delete a transaction",
            description = "Deletes the specified transaction from the budget."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @Parameter(description = "The budget ID", example = "1") @PathVariable Long budgetId,
            @Parameter(description = "The transaction ID", example = "10") @PathVariable Long transactionId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();

        // Delegate deletion
        transactionService.deleteTransaction(transactionId, username);

        return ResponseEntity.noContent().build(); // HTTP 204
    }
}
