# Stage 1: Build the application
FROM openjdk:17-jdk-alpine AS build
WORKDIR /workspace/app

# Copy maven wrapper and POM file
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make the maven wrapper executable
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-alpine
WORKDIR /app

# Add a volume pointing to /tmp for temporary files
VOLUME /tmp

# Create a non-root user to run the application
RUN addgroup -S procurement && adduser -S procurement -G procurement
USER procurement:procurement

# Copy JAR file from the build stage
COPY --from=build /workspace/app/target/*.jar procurement-workflow.jar

# Expose the application port
EXPOSE 8080

# Set health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Run the application with proper memory settings
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:prod}", "-jar", "procurement-workflow.jar"]
