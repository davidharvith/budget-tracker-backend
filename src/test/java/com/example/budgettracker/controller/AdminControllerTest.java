package com.example.budgettracker.controller;

import com.example.budgettracker.security.JwtAuthFilter;
import com.example.budgettracker.security.JwtUtil;
import com.example.budgettracker.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration-style unit tests for AdminController using MockMvc.
 *
 * This test class verifies role-based access to admin-only endpoints.
 */
@WebMvcTest(AdminController.class) // Bootstraps only the AdminController (lightweight MVC test)
@Import(com.example.budgettracker.config.SecurityConfig.class) // Imports the full security config
@AutoConfigureMockMvc(addFilters = false) // Disables security filters like JWT for controlled testing
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mocking security-related beans to prevent actual authentication logic
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    /**
     * Test that an authenticated user with the ADMIN role can access the protected admin endpoint.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminCanAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/only"))
                .andExpect(status().isOk())
                .andExpect(content().string("You are an admin!"));
    }

    /**
     * Test that an authenticated user with the USER role is forbidden from accessing the admin-only endpoint.
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void userCannotAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/only"))
                .andExpect(status().isForbidden());
    }
}
