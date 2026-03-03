# Stage 1: Build the application
FROM gradle:9.3.1-jdk25 AS builder
WORKDIR /app
COPY . .
# Build the JAR
RUN gradle bootJar --no-daemon

# Stage 2: Create the final image
FROM eclipse-temurin:25-jre
WORKDIR /app
# Copy the JAR from the builder stage
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar auth-service.jar
EXPOSE 8881
ENTRYPOINT ["java", "-jar", "auth-service.jar"]
