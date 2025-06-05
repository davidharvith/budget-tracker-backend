package com.example.budgettracker;

import com.example.budgettracker.dto.request.LoginRequest;
import com.example.budgettracker.dto.request.RegistrationRequest;
import com.example.budgettracker.model.Budget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for creating and retrieving budgets.
 * This test uses real HTTP calls with {@link TestRestTemplate} and an in-memory database
 * configured with the `local` profile.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local") // Use application-local.yml for isolated test config
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for potential MVC usage
public class BudgetIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String jwtToken;

    /**
     * Prepares a clean database, registers a test user, logs in,
     * and stores a JWT for authenticated requests.
     */
    @BeforeEach
    public void setup() {
        // Reset database tables between test runs
        jdbcTemplate.execute("TRUNCATE TABLE users, budgets, transactions RESTART IDENTITY CASCADE");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Register a test user
        RegistrationRequest registrationRequest = new RegistrationRequest("testuser", "testuser@example.com", "password123");
        ResponseEntity<String> registrationResponse = restTemplate.postForEntity(
                "/api/register",
                new HttpEntity<>(registrationRequest, headers),
                String.class
        );
        assertThat(registrationResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Log in and extract JWT token
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        ResponseEntity<TokenResponse> loginResponse = restTemplate.postForEntity(
                "/api/login",
                new HttpEntity<>(loginRequest, headers),
                TokenResponse.class
        );
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        jwtToken = loginResponse.getBody().token();
    }

    /**
     * Verifies that a budget can be created and then retrieved by the authenticated user.
     */
    @Test
    public void createAndGetBudget() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken); // Authenticate with JWT
        headers.setContentType(MediaType.APPLICATION_JSON);

        String budgetJson = "{\"name\":\"Integration Budget\", \"amount\": 1500.0}";

        // --- Create Budget ---
        ResponseEntity<Budget> createResponse = restTemplate.exchange(
                "/api/budgets",
                HttpMethod.POST,
                new HttpEntity<>(budgetJson, headers),
                Budget.class
        );
        System.out.println("[TEST] GET budgets response: " + createResponse.getBody());

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED); // Expect HTTP 201
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getName()).isEqualTo("Integration Budget");

        // --- Retrieve Budgets ---
        ResponseEntity<Budget[]> getResponse = restTemplate.exchange(
                "/api/budgets",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Budget[].class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotEmpty();
    }

    // Helper record for deserializing login token response
    public static record TokenResponse(String token) {}
}
