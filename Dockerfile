# Multi-stage Dockerfile for PSOFT Library Management System
# Stage 1: Build with Maven (optional - can use pre-built JAR)
# Stage 2: Runtime with OpenJDK 17

FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file
# Jenkins will build this before docker build, so target/*.jar will exist
COPY target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]