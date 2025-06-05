package com.example.budgettracker.controller;

import com.example.budgettracker.config.SecurityConfig;
import com.example.budgettracker.dto.request.RegistrationRequest;
import com.example.budgettracker.exception.DuplicateResourceException;
import com.example.budgettracker.exception.GlobalExceptionHandler;
import com.example.budgettracker.security.JwtAuthFilter;
import com.example.budgettracker.security.JwtUtil;
import com.example.budgettracker.service.CustomUserDetailsService;
import com.example.budgettracker.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link UserController}.
 *
 * These tests cover user registration scenarios including successful registration,
 * invalid input, and duplicate username handling.
 */
@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class}) // Import security and exception handling
@AutoConfigureMockMvc(addFilters = false) // Disable Spring Security filters
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class) // Disable auto security config
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    /**
     * Test case: Successful user registration.
     * 
     * Expected behavior: Returns HTTP 201 Created with success message.
     */
    @Test
    void registerUser_success_returns201() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "testuser", "test@example.com", "password123"
        );

        // Simulate successful registration
        doNothing().when(userService).registerUser(any(RegistrationRequest.class));

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully!"));
    }

    /**
     * Test case: Invalid input during registration (empty username).
     * 
     * Expected behavior: Returns HTTP 400 Bad Request with validation error message.
     */
    @Test
    void registerUser_invalidInput_returns400() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "", "test@example.com", "password123"
        );

        // No mocking needed — validation fails before service is called
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("username: Username is required"));
    }

    /**
     * Test case: Registration fails due to duplicate username.
     * 
     * Expected behavior: Returns HTTP 400 Bad Request with error message.
     */
    @Test
    void registerUser_duplicateUsername_returns400WithMessage() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "existinguser", "test@example.com", "password123"
        );

        // Simulate duplicate user error thrown by service
        doThrow(new DuplicateResourceException("Username is already taken."))
                .when(userService).registerUser(any(RegistrationRequest.class));

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username is already taken."));
    }
}
