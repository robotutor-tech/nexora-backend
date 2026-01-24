# Stage 1: Build stage
FROM gradle:8.10.0-jdk21-alpine AS builder

WORKDIR /build

# Copy only dependency-related files first for better layer caching
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle

# Download dependencies (this layer will be cached unless dependencies change)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src src

# Build the application
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root user for security
RUN addgroup -g 1001 -S nexora && \
    adduser -u 1001 -S nexora -G nexora

# Copy the JAR from builder stage
COPY --from=builder /build/build/libs/nexora-backend-*.jar app.jar

# Change ownership to non-root user
RUN chown nexora:nexora app.jar

# Switch to non-root user
USER nexora

# Expose the default Spring Boot port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# JVM optimization flags for containerized environments
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
