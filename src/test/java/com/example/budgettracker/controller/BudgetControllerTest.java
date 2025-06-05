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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit test for the BudgetController using Spring's WebMvcTest.
 * Mocks out the service layer and verifies controller behavior independently.
 */
@WebMvcTest(BudgetController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for isolated controller testing
@Import(SecurityConfig.class) // Import security config in case it's referenced
@ActiveProfiles("test") // Use test profile (in-memory DB, etc.)
public class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to simulate HTTP requests to controller

    // Inject mocked dependencies
    @MockitoBean
    private BudgetService budgetService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Test the POST /api/budgets endpoint.
     * Mocks the creation of a new budget and checks for correct JSON response.
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void createBudget_success() throws Exception {
        Budget mockBudget = new Budget("Test Budget", 1000.0, null);
        mockBudget.setId(1L); // Simulate persisted budget

        // Mock service behavior
        when(budgetService.createBudget(anyString(), eq("Test Budget"), eq(1000.0)))
                .thenReturn(mockBudget);

        // Perform POST request
        mockMvc.perform(post("/api/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Budget\", \"amount\":1000.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    /**
     * Test the GET /api/budgets endpoint.
     * Mocks retrieving all budgets for the authenticated user.
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getBudgets_success() throws Exception {
        Budget mockBudget = new Budget("Test Budget", 1000.0, null);
        mockBudget.setId(1L);

        when(budgetService.getBudgetsForUser(anyString()))
                .thenReturn(List.of(mockBudget));

        mockMvc.perform(get("/api/budgets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Budget"))
                .andExpect(jsonPath("$[0].amount").value(1000.0));
    }

    /**
     * Test the PUT /api/budgets/{id} endpoint.
     * Mocks updating a budget and verifies updated values are returned.
     */
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

    /**
     * Test the DELETE /api/budgets/{id} endpoint.
     * Mocks deleting a budget and ensures a 204 No Content is returned.
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void deleteBudget_success() throws Exception {
        doNothing().when(budgetService).deleteBudget(anyLong(), anyString());

        mockMvc.perform(delete("/api/budgets/1"))
                .andExpect(status().isNoContent());
    }
}
