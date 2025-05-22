package com.example.budgettracker.controller;

import com.example.budgettracker.config.SecurityConfig;
import com.example.budgettracker.model.Budget;
import com.example.budgettracker.security.JwtAuthFilter;
import com.example.budgettracker.security.JwtUtil;
import com.example.budgettracker.service.BudgetService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BudgetController.class)
@AutoConfigureMockMvc(addFilters = false) // Add this
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BudgetService budgetService;

    @MockitoBean // Add these security mocks
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void createBudget_success() throws Exception {
        Budget mockBudget = new Budget("Test Budget", 1000.0, null);
        mockBudget.setId(1L); // Set ID explicitly
        System.out.println("[TEST] Mock Budget ID: " + mockBudget.getId());
        when(budgetService.createBudget(anyString(), eq("Test Budget"),
                eq(1000.0))).thenReturn(mockBudget);

        mockMvc.perform(post("/api/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Budget\", \"amount\":1000.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getBudgets_success() throws Exception {
        Budget mockBudget = new Budget("Test Budget", 1000.0, null);
        mockBudget.setId(1L);

        when(budgetService.getBudgetsForUser(anyString()))
                .thenReturn(List.of(mockBudget));

        mockMvc.perform(get("/api/budgets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1)) // Now checks root array
                .andExpect(jsonPath("$[0].name").value("Test Budget"))
                .andExpect(jsonPath("$[0].amount").value(1000.0));

    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void updateBudget_success() throws Exception {
        Budget updatedBudget = new Budget("Updated Budget", 2000.0, null);
        updatedBudget.setId(1L);

        when(budgetService.updateBudget(anyLong(), anyString(), anyString(), anyDouble()))
                .thenReturn(updatedBudget);

        mockMvc.perform(put("/api/budgets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "name": "Updated Budget",
                        "amount": 2000.0
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Budget"))
                .andExpect(jsonPath("$.amount").value(2000.0));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void deleteBudget_success() throws Exception {
        doNothing().when(budgetService).deleteBudget(anyLong(), anyString());

        mockMvc.perform(delete("/api/budgets/1"))
                .andExpect(status().isNoContent());
    }
}
