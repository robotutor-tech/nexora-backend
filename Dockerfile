FROM gradle:8.10.0-jdk21-alpine

WORKDIR /app

COPY build/libs/nexora-0.0.1-SNAPSHOT.jar ./app.jar

CMD ["java", "-jar", "app.jar"]