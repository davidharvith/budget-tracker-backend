# Use the official OpenJDK image as a base
FROM eclipse-temurin:21-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file into the container
COPY target/budget-tracker-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your app runs on
EXPOSE 8080
# ================================
# Backend Dockerfile (Spring Boot)
# ================================

# Base image: OpenJDK 21 runtime
FROM eclipse-temurin:21-jre

# Set working directory inside container
WORKDIR /app

# Copy the Spring Boot JAR from the local build context into the container
COPY target/budget-tracker-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 (Spring Boot default)
EXPOSE 8080


# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
