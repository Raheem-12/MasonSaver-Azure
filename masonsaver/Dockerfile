# Stage 1: Build the Spring Boot application
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven configuration first for dependency caching
COPY pom.xml .

RUN mvn dependency:go-offline -B

# Copy source code and build the application
COPY src ./src

RUN mvn clean package -DskipTests


# Stage 2: Run the packaged application
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]