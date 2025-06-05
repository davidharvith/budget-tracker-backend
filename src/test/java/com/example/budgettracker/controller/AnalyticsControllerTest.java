package com.example.budgettracker.controller;

import com.example.budgettracker.config.SecurityConfig;
import com.example.budgettracker.dto.response.CategorySummary;
import com.example.budgettracker.dto.response.MonthlySummary;
import com.example.budgettracker.model.Transaction;
import com.example.budgettracker.security.JwtAuthFilter;
import com.example.budgettracker.security.JwtUtil;
import com.example.budgettracker.service.AnalyticsService;
import com.example.budgettracker.service.CustomUserDetailsService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AnalyticsController endpoints.
 * These tests verify that category and monthly summary analytics endpoints
 * return correct JSON responses given mock service data.
 */
@WebMvcTest(AnalyticsController.class) // Load only AnalyticsController and MVC infrastructure
@AutoConfigureMockMvc(addFilters = false) // Disable filters (e.g., JWT) to isolate controller logic
@Import(SecurityConfig.class) // Import security config in case route-level restrictions are tested
public class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyticsService analyticsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Test the GET /api/budgets/{id}/analytics/category endpoint.
     * Simulates an authenticated user and mocks the analytics service to return
     * category-wise expense totals.
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getCategoryAnalytics_success() throws Exception {
        // Mock result
        List<CategorySummary> mockSummaries = List.of(
                new CategorySummary("Groceries", 250.0),
                new CategorySummary("Utilities", 100.0)
        );

        // Mock service behavior
        when(analyticsService.sumByCategory(anyLong(), any(Transaction.Type.class), anyString()))
                .thenReturn(mockSummaries);

        // Perform GET request and validate JSON response structure and values
        mockMvc.perform(get("/api/budgets/1/analytics/category")
                        .param("type", "EXPENSE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Groceries"))
                .andExpect(jsonPath("$[0].total").value(250.0))
                .andExpect(jsonPath("$[1].category").value("Utilities"))
                .andExpect(jsonPath("$[1].total").value(100.0));
    }

    /**
     * Test the GET /api/budgets/{id}/analytics/month endpoint.
     * Simulates an authenticated user and mocks the analytics service to return
     * income totals grouped by month.
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getMonthlyAnalytics_success() throws Exception {
        // Mock result
        List<MonthlySummary> mockSummaries = List.of(
                new MonthlySummary(2025, 5, 1500.0),
                new MonthlySummary(2025, 6, 1200.0)
        );

        // Mock service behavior
        when(analyticsService.sumByMonth(anyLong(), any(Transaction.Type.class), anyString()))
                .thenReturn(mockSummaries);

        // Perform GET request and validate JSON response
        MvcResult result = mockMvc.perform(get("/api/budgets/1/analytics/month")
                        .param("type", "INCOME")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].year").value(2025))
                .andExpect(jsonPath("$[0].month").value(5))
                .andExpect(jsonPath("$[0].total").value(1500.0))
                .andExpect(jsonPath("$[1].year").value(2025))
                .andExpect(jsonPath("$[1].month").value(6))
                .andExpect(jsonPath("$[1].total").value(1200.0))
                .andReturn();

        // Optional: Print raw response for debugging purposes
        System.out.println("Response: " + result.getResponse().getContentAsString());
    }
}
