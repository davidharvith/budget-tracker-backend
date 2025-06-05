package com.example.budgettracker;

import com.example.budgettracker.dto.request.LoginRequest;
import com.example.budgettracker.dto.request.RegistrationRequest;
import com.example.budgettracker.dto.request.TransactionRequest;
import com.example.budgettracker.model.Transaction.Type;
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
 * Integration test for transaction endpoints.
 * Validates that transactions can be created and retrieved correctly.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local") // Use the local test profile with a separate database
public class TransactionIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String jwtToken;
    private Long budgetId;

    /**
     * Sets up the test:
     * - Registers a unique user
     * - Logs the user in to obtain a JWT
     * - Creates a budget under the user's account
     */
    @BeforeEach
    public void setup() {
        // Generate a unique user for test isolation
        String unique = UUID.randomUUID().toString().substring(0, 8);
        String username = "testuser" + unique;
        String email = username + "@example.com";
        String password = "password123";

        // Register the user
        RegistrationRequest registrationRequest = new RegistrationRequest(username, email, password);
        restTemplate.postForEntity("/api/register", registrationRequest, String.class);

        // Log the user in and get the JWT token
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
        budgetId = createBudgetResponse.getBody().getId(); // Store budget ID for use in test
    }

    /**
     * Tests adding a transaction to a budget and retrieving it.
     * Validates both POST and GET endpoints for transactions.
     */
    @Test
    public void addAndGetTransaction() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create a transaction request
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(200.0);
        transactionRequest.setType(Type.EXPENSE);
        transactionRequest.setDescription("Groceries");

        // Send POST request to add the transaction
        ResponseEntity<TransactionResponse> addResponse = restTemplate.exchange(
                "/api/budgets/" + budgetId + "/transactions",
                HttpMethod.POST,
                new HttpEntity<>(transactionRequest, headers),
                TransactionResponse.class
        );

        // Verify that the transaction was created
        assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(addResponse.getBody()).isNotNull();
        assertThat(addResponse.getBody().getAmount()).isEqualTo(200.0);

        // Send GET request to fetch all transactions for the budget
        ResponseEntity<TransactionResponse[]> getResponse = restTemplate.exchange(
                "/api/budgets/" + budgetId + "/transactions",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                TransactionResponse[].class
        );

        // Verify the transaction appears in the list
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotEmpty();
        assertThat(getResponse.getBody()[0].getDescription()).isEqualTo("Groceries");
    }

    // --- Helper classes for response deserialization ---

    /**
     * Used to extract the JWT token from the login response.
     */
    public static record TokenResponse(String token) {}

    /**
     * Used to extract the budget ID from the create budget response.
     */
    public static class BudgetResponse {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    /**
     * Used to deserialize transaction responses.
     */
    public static class TransactionResponse {
        private Long id;
        private Double amount;
        private Type type;
        private String description;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public Type getType() { return type; }
        public void setType(Type type) { this.type = type; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
