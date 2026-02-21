FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S nexora && \
    adduser -u 1001 -S nexora -G nexora

# Copy JAR built by GitHub Actions
COPY build/libs/*.jar app.jar

# Change ownership
RUN chown nexora:nexora app.jar

USER nexora

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]