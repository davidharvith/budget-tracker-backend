package com.example.budgettracker.controller;

import com.example.budgettracker.config.SecurityConfig;
import com.example.budgettracker.dto.response.BudgetSummaryResponse;
import com.example.budgettracker.security.JwtAuthFilter;
import com.example.budgettracker.security.JwtUtil;
import com.example.budgettracker.service.BudgetSummaryService;
import com.example.budgettracker.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit test for {@link BudgetSummaryController}.
 * 
 * This test ensures that the controller correctly returns a budget summary
 * when accessed by an authenticated user. It uses MockMvc to simulate HTTP
 * requests and mocks service dependencies for isolation.
 */
@WebMvcTest(BudgetSummaryController.class) // Load only the controller and MVC layer
@Import(SecurityConfig.class)              // Import security configuration for route protection simulation
@AutoConfigureMockMvc(addFilters = false)  // Disable filters (e.g., JWT filter) to isolate controller logic
public class BudgetSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc; // Allows testing of controller endpoints via mock HTTP requests

    @MockitoBean
    private BudgetSummaryService budgetSummaryService; // Mocked service used by the controller

    @MockitoBean
    private JwtUtil jwtUtil; // Mocked JWT utility bean (unused in test, required by context)

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter; // Mocked JWT filter bean (disabled in test)

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService; // Required mock to satisfy Spring Security context

    /**
     * Test for GET /api/budgets/{id}/summary.
     * 
     * This test verifies that an authenticated user with role USER
     * can successfully retrieve the budget summary for a given budget ID.
     * The service layer is mocked to return a static response.
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getBudgetSummary_success() throws Exception {
        // Arrange: mock the expected summary response from the service
        BudgetSummaryResponse mockSummary = new BudgetSummaryResponse(1L, 1200.0, 800.0, 400.0);
        when(budgetSummaryService.getBudgetSummary(anyLong(), anyString()))
                .thenReturn(mockSummary);

        // Act & Assert: perform GET request and verify the JSON response fields and values
        mockMvc.perform(get("/api/budgets/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.budgetId").value(1))
                .andExpect(jsonPath("$.totalIncome").value(1200.0))
                .andExpect(jsonPath("$.totalExpense").value(800.0))
                .andExpect(jsonPath("$.balance").value(400.0));
    }
}
