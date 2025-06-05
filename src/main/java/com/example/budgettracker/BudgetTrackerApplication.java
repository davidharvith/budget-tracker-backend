package com.example.budgettracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Budget Tracker application.
 * 
 * @SpringBootApplication is a convenience annotation that:
 * - Enables component scanning
 * - Enables auto-configuration
 * - Marks this class as a Spring Boot application root
 */
@SpringBootApplication
public class BudgetTrackerApplication {

    /**
     * Launches the Spring Boot application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(BudgetTrackerApplication.class, args);
    }
}
