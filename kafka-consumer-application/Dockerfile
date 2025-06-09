# Stage 1: Maven build with Java 21
FROM maven:3.9.4-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run with lightweight JDK 21
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Use sh -c so that environment variables are resolved correctly
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
