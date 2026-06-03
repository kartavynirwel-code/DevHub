# Build phase using multi-stage engine to reduce final image size
FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runnable execution environment image footprint 
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/target/devhub-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]