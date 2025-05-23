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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BudgetSummaryIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String jwtToken;
    private Long budgetId;

    @BeforeEach
    public void setup() {
        // Register and login user
        String unique = UUID.randomUUID().toString().substring(0, 8);
        String username = "testuser" + unique;
        String email = username + "@example.com";
        String password = "password123";

        RegistrationRequest registrationRequest = new RegistrationRequest(username, email, password);
        restTemplate.postForEntity("/api/register", registrationRequest, String.class);

        LoginRequest loginRequest = new LoginRequest(username, password);
        ResponseEntity<TokenResponse> loginResponse = restTemplate.postForEntity("/api/login", loginRequest, TokenResponse.class);
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
        budgetId = createBudgetResponse.getBody().getId();
    }

    @Test
    public void getBudgetSummary_success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);

        ResponseEntity<BudgetSummaryResponse> response = restTemplate.exchange(
                "/api/budgets/" + budgetId + "/summary",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                BudgetSummaryResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBudgetId()).isEqualTo(budgetId);
        // totalIncome, totalExpense, balance can be zero if no transactions yet
    }

    // Helper classes for deserialization
    public static record TokenResponse(String token) {}
    public static class BudgetResponse {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }
}
