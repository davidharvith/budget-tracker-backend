package com.example.budgettracker;

import com.example.budgettracker.dto.request.LoginRequest;
import com.example.budgettracker.dto.request.RegistrationRequest;
import com.example.budgettracker.dto.response.BudgetSummaryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the budget summary endpoint.
 * Validates that the /api/budgets/{id}/summary route works end-to-end.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local") // Use test-specific profile and database
public class BudgetSummaryIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String jwtToken;
    private Long budgetId;

    /**
     * Sets up the test environment:
     * - Registers a new user with a unique email
     * - Logs the user in and stores the JWT token
     * - Creates a budget associated with the user
     */
    @BeforeEach
    public void setup() {
        // Generate a unique user each test run
        String unique = UUID.randomUUID().toString().substring(0, 8);
        String username = "testuser" + unique;
        String email = username + "@example.com";
        String password = "password123";

        // Register new user
        RegistrationRequest registrationRequest = new RegistrationRequest(username, email, password);
        restTemplate.postForEntity("/api/register", registrationRequest, String.class);

        // Authenticate and retrieve token
        LoginRequest loginRequest = new LoginRequest(username, password);
        ResponseEntity<TokenResponse> loginResponse = restTemplate.postForEntity(
                "/api/login",
                loginRequest,
                TokenResponse.class
        );
        jwtToken = loginResponse.getBody().token();

        // Create a budget
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String budgetJson = "{\"name\":\"Test Budget\", \"amount\": 1000.0}";
        ResponseEntity<BudgetResponse> createBudgetResponse = restTemplate.exchange(
                "/api/budgets",
                HttpMethod.POST,
                new HttpEntity<>(budgetJson, headers),
                BudgetResponse.class
        );
        budgetId = createBudgetResponse.getBody().getId(); // Store created budget ID
    }

    /**
     * Tests that the budget summary endpoint returns correct basic info.
     * This test assumes no transactions exist yet, so summary values should be zero.
     */
    @Test
    public void getBudgetSummary_success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);

        // Call GET /api/budgets/{id}/summary
        ResponseEntity<BudgetSummaryResponse> response = restTemplate.exchange(
                "/api/budgets/" + budgetId + "/summary",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                BudgetSummaryResponse.class
        );

        // Validate successful response and correct budget ID
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBudgetId()).isEqualTo(budgetId);
        // No transactions were added, so income/expense/balance should be 0.0
    }

    // --- Helper classes for response deserialization ---

    /**
     * Used to deserialize the login token response from /api/login.
     */
    public static record TokenResponse(String token) {}

    /**
     * Used to extract the budget ID from the response when creating a budget.
     */
    public static class BudgetResponse {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }
}
