Budget Tracker API
==================

A robust, secure, and fully-tested RESTful API for managing personal budgets, transactions, and financial analytics.
Built with Spring Boot, PostgreSQL, JWT authentication, and Swagger/OpenAPI documentation.

------------------------------------------------------------
🚀 Features

- User Registration & JWT Authentication
- Role-based Access Control (User/Admin)
- Budgets: Create, update, delete, and list budgets per user
- Transactions: Add, update, delete, and list transactions per budget (with category, date, and description)
- Analytics:
    - By category (e.g., Groceries, Utilities)
    - By month (income/expense trends)
- Budget Summaries: Get total income, expenses, and balance for each budget
- Comprehensive Testing: Controller and integration tests
- Interactive API Documentation: Swagger UI

------------------------------------------------------------
🛠️ Tech Stack

- Java 21, Spring Boot 3
- Spring Security (JWT)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Swagger/OpenAPI (springdoc-openapi)
- JUnit 5, Mockito

------------------------------------------------------------
📦 Getting Started

Prerequisites:
- Java 21+
- Maven 3.8+
- PostgreSQL (running locally or in Docker)

Setup:
1. Clone the repository
   git clone https://github.com/yourusername/budget-tracker.git
   cd budget-tracker

2. Configure your database
   Edit src/main/resources/application.properties:
   spring.datasource.url=jdbc:postgresql://localhost:5432/budgetdb
   spring.datasource.username=your_db_user
   spring.datasource.password=your_db_password

3. Run the application
   mvn spring-boot:run

4. Access Swagger UI
   http://localhost:8080/swagger-ui.html

------------------------------------------------------------
🔐 Authentication

This API uses JWT Bearer tokens.

1. Register
   POST /api/register
   Body:
   {
     "username": "johndoe",
     "email": "john@example.com",
     "password": "password123"
   }

2. Login
   POST /api/login
   Body:
   {
     "username": "johndoe",
     "password": "password123"
   }
   Response:
   { "token": "eyJhbGciOiJIUzI1NiIsInR..." }

3. Use the token
   Add this header to all protected requests:
   Authorization: Bearer <your-token-here>

4. Try it in Swagger UI
   Click "Authorize" and paste your token to make authenticated requests.

------------------------------------------------------------
🧪 Running Tests

mvn test
- Runs all unit, controller, and integration tests.

------------------------------------------------------------
📊 API Documentation

- Interactive docs: http://localhost:8080/swagger-ui.html
- OpenAPI spec:    http://localhost:8080/v3/api-docs

------------------------------------------------------------
📝 Example Endpoints

- POST /api/register – Register a new user
- POST /api/login – Login and get JWT token
- GET /api/budgets – List user budgets
- POST /api/budgets – Create a budget
- GET /api/budgets/{budgetId}/transactions – List transactions for a budget
- POST /api/budgets/{budgetId}/transactions – Add a transaction
- GET /api/budgets/{budgetId}/analytics/category?type=EXPENSE – Analytics by category
- GET /api/budgets/{budgetId}/analytics/month?type=EXPENSE – Analytics by month
- GET /api/budgets/{budgetId}/summary – Budget summary

See Swagger UI for full details and try-it-out functionality.

------------------------------------------------------------
🚦 Roadmap / TODO

- [ ] User profile management (update info, change password)
- [ ] Budget limits & alerts
- [ ] Weekly/quarterly analytics
- [ ] Export to CSV/PDF
- [ ] Admin features (user management, audit logs)
- [ ] Dockerfile & docker-compose
- [ ] CI/CD pipeline

------------------------------------------------------------
🤝 Contributing

Pull requests welcome! For major changes, please open an issue first to discuss what you’d like to change.

------------------------------------------------------------
👤 Contact

David Harvith
davidaviadh@gmail.com

------------------------------------------------------------

