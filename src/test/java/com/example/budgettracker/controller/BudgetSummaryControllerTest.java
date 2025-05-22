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

@WebMvcTest(BudgetSummaryController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters
public class BudgetSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BudgetSummaryService budgetSummaryService;

    // Add these security mocks
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getBudgetSummary_success() throws Exception {
        BudgetSummaryResponse mockSummary = new BudgetSummaryResponse(1L, 1200.0, 800.0);

        when(budgetSummaryService.getBudgetSummary(anyLong(), anyString()))
                .thenReturn(mockSummary);

        mockMvc.perform(get("/api/budgets/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.budgetId").value(1))
                .andExpect(jsonPath("$.totalIncome").value(1200.0))
                .andExpect(jsonPath("$.totalExpense").value(800.0))
                .andExpect(jsonPath("$.balance").value(400.0));
    }
}
