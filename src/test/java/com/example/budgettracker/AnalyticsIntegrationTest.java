package com.example.budgettracker;

import com.example.budgettracker.dto.request.LoginRequest;
import com.example.budgettracker.dto.request.RegistrationRequest;
import com.example.budgettracker.dto.request.TransactionRequest;
import com.example.budgettracker.model.Transaction;
import com.example.budgettracker.dto.response.BudgetSummaryResponse;
import com.example.budgettracker.dto.response.CategorySummary;
import com.example.budgettracker.dto.response.MonthlySummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public class AnalyticsIntegrationTest {

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

        String budgetJson = "{\"name\":\"Test Budget\", \"amount\":1000.0}";
        ResponseEntity<BudgetResponse> createBudgetResponse = restTemplate.exchange(
                "/api/budgets",
                HttpMethod.POST,
                new HttpEntity<>(budgetJson, headers),
                BudgetResponse.class
        );
        budgetId = createBudgetResponse.getBody().getId();

        // Add transactions with categories and dates
        TransactionRequest t1 = new TransactionRequest();
        t1.setAmount(200.0);
        t1.setType(Transaction.Type.EXPENSE);
        t1.setDescription("Groceries");
        t1.setCategory("Groceries");
        t1.setDate(LocalDateTime.of(2025, 5, 1, 12, 0));

        TransactionRequest t2 = new TransactionRequest();
        t2.setAmount(100.0);
        t2.setType(Transaction.Type.EXPENSE);
        t2.setDescription("Utilities");
        t2.setCategory("Utilities");
        t2.setDate(LocalDateTime.of(2025, 5, 15, 12, 0));

        TransactionRequest t3 = new TransactionRequest();
        t3.setAmount(1200.0);
        t3.setType(Transaction.Type.INCOME);
        t3.setDescription("Salary");
        t3.setCategory("Salary");
        t3.setDate(LocalDateTime.of(2025, 6, 1, 12, 0));

        // Post transactions
        restTemplate.exchange(
                "/api/budgets/" + budgetId + "/transactions",
                HttpMethod.POST,
                new HttpEntity<>(t1, headers),
                Object.class
        );
        restTemplate.exchange(
                "/api/budgets/" + budgetId + "/transactions",
                HttpMethod.POST,
                new HttpEntity<>(t2, headers),
                Object.class
        );
        restTemplate.exchange(
                "/api/budgets/" + budgetId + "/transactions",
                HttpMethod.POST,
                new HttpEntity<>(t3, headers),
                Object.class
        );

        // --- Debug printout: fetch and print all transactions for this budget ---
        ResponseEntity<String> txDebugResponse = restTemplate.exchange(
                "/api/budgets/" + budgetId + "/transactions",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        System.out.println("Saved transactions for budget " + budgetId + ": " + txDebugResponse.getBody());
    }


    @Test
    public void getCategoryAnalytics_success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);

        // 1. Print the raw JSON response
        ResponseEntity<String> rawResponse = restTemplate.exchange(
                "/api/budgets/" + budgetId + "/analytics/category?type=EXPENSE",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        System.out.println("Category analytics raw response: " + rawResponse.getBody());
        System.out.println("Status: " + rawResponse.getStatusCode());

        // 2. Parse and assert as before
        ResponseEntity<CategorySummary[]> response = restTemplate.exchange(
                "/api/budgets/" + budgetId + "/analytics/category?type=EXPENSE",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                CategorySummary[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(2);

        // Check that categories and totals are correct
        boolean foundGroceries = false, foundUtilities = false;
        for (CategorySummary cs : response.getBody()) {
            if (cs.category().equals("Groceries")) {
                assertThat(cs.total()).isEqualTo(200.0);
                foundGroceries = true;
            }
            if (cs.category().equals("Utilities")) {
                assertThat(cs.total()).isEqualTo(100.0);
                foundUtilities = true;
            }
        }
        assertThat(foundGroceries).isTrue();
        assertThat(foundUtilities).isTrue();
    }

    @Test
    public void getMonthlyAnalytics_success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);

        // 1. Print the raw JSON response
        ResponseEntity<String> rawResponse = restTemplate.exchange(
                "/api/budgets/" + budgetId + "/analytics/month?type=EXPENSE",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        System.out.println("Monthly analytics raw response: " + rawResponse.getBody());
        System.out.println("Status: " + rawResponse.getStatusCode());

        // 2. Parse and assert as before
        ResponseEntity<MonthlySummary[]> response = restTemplate.exchange(
                "/api/budgets/" + budgetId + "/analytics/month?type=EXPENSE",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                MonthlySummary[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(1);

        boolean foundMay = false;
        for (MonthlySummary ms : response.getBody()) {
            if (ms.year() == 2025 && ms.month() == 5) {
                assertThat(ms.total()).isEqualTo(300.0); // 200+100 in May
                foundMay = true;
            }
        }
        assertThat(foundMay).isTrue();
    }

    // Helper classes for deserialization
    public static record TokenResponse(String token) {}
    public static class BudgetResponse {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }
}
