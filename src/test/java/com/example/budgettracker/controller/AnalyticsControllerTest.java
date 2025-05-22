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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SecurityConfig.class)
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

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getCategoryAnalytics_success() throws Exception {
        List<CategorySummary> mockSummaries = List.of(
                new CategorySummary("Groceries", 250.0),
                new CategorySummary("Utilities", 100.0)
        );

        when(analyticsService.sumByCategory(anyLong(), any(Transaction.Type.class), anyString()))
                .thenReturn(mockSummaries);

        mockMvc.perform(get("/api/budgets/1/analytics/category")
                        .param("type", "EXPENSE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Groceries"))
                .andExpect(jsonPath("$[0].total").value(250.0))
                .andExpect(jsonPath("$[1].category").value("Utilities"))
                .andExpect(jsonPath("$[1].total").value(100.0));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getMonthlyAnalytics_success() throws Exception {
        List<MonthlySummary> mockSummaries = List.of(
                new MonthlySummary(2025, 5, 1500.0),
                new MonthlySummary(2025, 6, 1200.0)
        );

        when(analyticsService.sumByMonth(anyLong(), any(Transaction.Type.class), anyString()))
                .thenReturn(mockSummaries);

        MvcResult result = mockMvc.perform(get("/api/budgets/1/analytics/month")
                        .param("type", "INCOME")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].year").value(2025))
                .andExpect(jsonPath("$[0].month").value(5))
                .andExpect(jsonPath("$[0].total").value(1500.0))
                .andExpect(jsonPath("$[1].year").value(2025))
                .andExpect(jsonPath("$[1].month").value(6))
                .andExpect(jsonPath("$[1].total").value(1200.0)).andReturn();
        System.out.println("Response: weird" + result.getResponse().getContentAsString());
    }
}
