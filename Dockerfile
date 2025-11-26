# Use a lightweight JDK image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built jar
COPY build/libs/*.jar app.jar

# Expose port
EXPOSE 8082

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
