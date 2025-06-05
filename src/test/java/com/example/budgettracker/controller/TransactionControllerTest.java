package com.example.budgettracker.controller;

import com.example.budgettracker.config.SecurityConfig;
import com.example.budgettracker.model.Transaction;
import com.example.budgettracker.model.Transaction.Type;
import com.example.budgettracker.security.JwtAuthFilter;
import com.example.budgettracker.security.JwtUtil;
import com.example.budgettracker.service.CustomUserDetailsService;
import com.example.budgettracker.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link TransactionController}.
 *
 * This class uses MockMvc to simulate HTTP requests to transaction endpoints and verifies
 * the expected behavior and JSON responses using mocked dependencies.
 */
@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable Spring Security filters for isolation
@Import(SecurityConfig.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Tests POST /api/budgets/{budgetId}/transactions.
     * Verifies that a new transaction is created successfully and returns correct JSON.
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void addTransaction_success() throws Exception {
        Transaction mockTransaction = new Transaction(100.0, Type.INCOME, "Salary", LocalDate.now(), "Food", null);
        mockTransaction.setId(1L);

        when(transactionService.addTransaction(anyLong(), any(Transaction.class), anyString()))
                .thenReturn(mockTransaction);

        String requestJson = """
            {
                "amount": 100.0,
                "type": "INCOME",
                "description": "Salary"
            }
        """;

        mockMvc.perform(post("/api/budgets/1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.description").value("Salary"));
    }

    /**
     * Tests GET /api/budgets/{budgetId}/transactions.
     * Verifies that a list of transactions is returned successfully.
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getTransactions_success() throws Exception {
        Transaction mockTransaction = new Transaction(100.0, Type.INCOME, "Salary", LocalDate.now(), "Food", null);
        mockTransaction.setId(1L);

        when(transactionService.getTransactions(anyLong(), anyString()))
                .thenReturn(List.of(mockTransaction));

        mockMvc.perform(get("/api/budgets/1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[0].type").value("INCOME"))
                .andExpect(jsonPath("$[0].description").value("Salary"));
    }

    /**
     * Tests PUT /api/budgets/{budgetId}/transactions/{transactionId}.
     * Verifies that an existing transaction is updated correctly.
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void updateTransaction_success() throws Exception {
        Transaction updatedTransaction = new Transaction(150.0, Type.INCOME, "Bonus", LocalDate.now(), "Food", null);
        updatedTransaction.setId(1L);

        when(transactionService.updateTransaction(anyLong(), any(Transaction.class), anyString()))
                .thenReturn(updatedTransaction);

        String requestJson = """
            {
                "amount": 150.0,
                "type": "INCOME",
                "description": "Bonus"
            }
        """;

        mockMvc.perform(put("/api/budgets/1/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(150.0))
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.description").value("Bonus"));
    }

    /**
     * Tests DELETE /api/budgets/{budgetId}/transactions/{transactionId}.
     * Verifies that the transaction is deleted and returns 204 No Content.
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void deleteTransaction_success() throws Exception {
        doNothing().when(transactionService).deleteTransaction(anyLong(), anyString());

        mockMvc.perform(delete("/api/budgets/1/transactions/1"))
                .andExpect(status().isNoContent());
    }
}
