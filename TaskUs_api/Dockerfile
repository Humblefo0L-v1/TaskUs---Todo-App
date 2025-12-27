FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first (for better Docker layer caching)
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port (Render will override this with PORT env variable)
EXPOSE 8080

# Set environment variable for Spring profile
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
# Use $PORT environment variable that Render provides
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]