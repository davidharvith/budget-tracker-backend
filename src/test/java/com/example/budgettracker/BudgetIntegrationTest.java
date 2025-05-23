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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class BudgetIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String jwtToken;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {

        jdbcTemplate.execute("TRUNCATE TABLE users, budgets, transactions RESTART IDENTITY CASCADE");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Register user
        RegistrationRequest registrationRequest = new RegistrationRequest("testuser", "testuser@example.com", "password123");
        ResponseEntity<String> registrationResponse = restTemplate.postForEntity(
                "/api/register",
                new HttpEntity<>(registrationRequest, headers),
                String.class
        );
        assertThat(registrationResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Login
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        ResponseEntity<TokenResponse> loginResponse = restTemplate.postForEntity(
                "/api/login",
                new HttpEntity<>(loginRequest, headers),
                TokenResponse.class
        );
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        jwtToken = loginResponse.getBody().token();
    }


    @Test
    public void createAndGetBudget() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String budgetJson = "{\"name\":\"Integration Budget\", \"amount\": 1500.0}";

        // Create budget
        ResponseEntity<Budget> createResponse = restTemplate.exchange(
                "/api/budgets",
                HttpMethod.POST,
                new HttpEntity<>(budgetJson, headers),
                Budget.class
        );
        System.out.println("[TEST] GET budgets response: " + createResponse.getBody());
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED); // 201 for created
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getName()).isEqualTo("Integration Budget");

        // Get budgets
        ResponseEntity<Budget[]> getResponse = restTemplate.exchange(
                "/api/budgets",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Budget[].class
        );
        assertThat(getResponse.getBody()).isNotEmpty();

    }

    // Helper record for token deserialization
    public static record TokenResponse(String token) {}
}
