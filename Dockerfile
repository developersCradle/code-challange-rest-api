# Has Maven.
FROM maven:3.8.8-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Run maven.
RUN mvn clean package -DskipTests

# Next, run the application.
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/*.jar rest-demo.jar

ENTRYPOINT ["java", "-jar", "rest-demo.jar"]