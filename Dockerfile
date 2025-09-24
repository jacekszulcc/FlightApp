FROM maven:3.8.5-openjdk-17 AS builder

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/flight-app-service/target/flight-app-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]