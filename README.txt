Budget Tracker API
A robust, secure, and fully-tested RESTful API for managing personal budgets, transactions, and financial analytics.
Built with Spring Boot, PostgreSQL, JWT authentication, and Swagger/OpenAPI documentation.

🚀 Features
User Registration & JWT Authentication

Role-based Access Control (User/Admin)

Budgets: Create, update, delete, and list budgets per user

Transactions: Add, update, delete, and list transactions per budget (with category, date, and description)

Analytics:

By category (e.g., Groceries, Utilities)

By month (income/expense trends)

Budget Summaries: Get total income, expenses, and balance for each budget

Comprehensive Testing: Controller and integration tests

Interactive API Documentation: Swagger UI

🛠️ Tech Stack
Java 21, Spring Boot 3

Spring Security (JWT)

Spring Data JPA (Hibernate)

PostgreSQL

Swagger/OpenAPI (springdoc-openapi)

JUnit 5, Mockito

Docker, Docker Compose

📦 Getting Started
Prerequisites:

Java 21+

Maven 3.8+

PostgreSQL (running locally or in Docker)

Docker & Docker Compose (optional, for containerized setup)

Setup:

Clone the repository

text
git clone https://github.com/yourusername/budget-tracker.git
cd budget-tracker
Create your local configuration

⚠️ Important:
This project does not include a committed application.properties file for security reasons.
You must create your own src/main/resources/application.properties with your database and secret values.

Example application.properties:

text
spring.datasource.url=jdbc:postgresql://localhost:5432/budgetdb
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password

jwt.secret=your_jwt_secret
jwt.expirationMs=86400000
Never commit your real secrets to Git.

You may copy and rename this as application.properties.example for sharing a template.

Run the application

text
mvn spring-boot:run
Access Swagger UI

http://localhost:8080/swagger-ui.html

🐳 Docker & Docker Compose
This project supports containerized development and deployment using Docker and Docker Compose.

Dockerfile
A production-ready Dockerfile is included:

text
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/budget-tracker-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
docker-compose.yml (example, not committed)
text
services:
  db:
    image: postgres:16-alpine
    container_name: postgres-db
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: budgetdb
    ports:
      - "5432:5432"
    volumes:
      - db-data:/var/lib/postgresql/data

  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: budget-tracker-app
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/budgetdb
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
      JWT_SECRET: your_jwt_secret
    ports:
      - "8080:8080"
    restart: always

volumes:
  db-data:
Note:
Do not commit your real docker-compose.yml if it contains secrets.
Use environment variables or a .env file (add to .gitignore) for sensitive values.

🔐 Authentication
This API uses JWT Bearer tokens.

Register

POST /api/register

Body:

json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
Login

POST /api/login

Body:

json
{
  "username": "johndoe",
  "password": "password123"
}
Response:

json
{ "token": "eyJhbGciOiJIUzI1NiIsInR..." }
Use the token

Add this header to all protected requests:

text
Authorization: Bearer <your-token-here>
Try it in Swagger UI

Click "Authorize" and paste your token to make authenticated requests.

🧪 Running Tests
text
mvn test
Runs all unit, controller, and integration tests.

📊 API Documentation
Interactive docs: http://localhost:8080/swagger-ui.html

OpenAPI spec: http://localhost:8080/v3/api-docs

📝 Example Endpoints
POST /api/register – Register a new user

POST /api/login – Login and get JWT token

GET /api/budgets – List user budgets

POST /api/budgets – Create a budget

GET /api/budgets/{budgetId}/transactions – List transactions for a budget

POST /api/budgets/{budgetId}/transactions – Add a transaction

GET /api/budgets/{budgetId}/analytics/category?type=EXPENSE – Analytics by category

GET /api/budgets/{budgetId}/analytics/month?type=EXPENSE – Analytics by month

GET /api/budgets/{budgetId}/summary – Budget summary

See Swagger UI for full details and try-it-out functionality.

🚦 Roadmap / TODO
 User profile management (update info, change password)

 Budget limits & alerts

 Weekly/quarterly analytics

 Export to CSV/PDF

 Admin features (user management, audit logs)

 Dockerfile & docker-compose

 CI/CD pipeline

🤝 Contributing
Pull requests welcome! For major changes, please open an issue first to discuss what you’d like to change.

👤 Contact
David Harvith
davidaviadh@gmail.com
