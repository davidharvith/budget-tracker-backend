package com.example.budgettracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for customizing OpenAPI/Swagger documentation.
 * 
 * This sets up metadata and JWT-based bearer authentication so that 
 * endpoints requiring security can be properly documented and tested.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates and configures the OpenAPI specification for Swagger UI.
     * 
     * - Sets API title, version, and description.
     * - Configures a security scheme for JWT Bearer token authentication.
     * 
     * @return customized OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Budget Tracker API")
                        .version("1.0")
                        .description("A RESTful API for managing personal budgets, transactions, and analytics."))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
