# Use an official OpenJDK runtime as the base image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the entire project
COPY . .

# Install Maven
RUN apt-get update && \
    apt-get install -y maven wget && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Build the application
RUN mvn clean package -DskipTests

# Find and copy the JAR file
RUN find /app/target -name "*.jar" -exec cp {} /app/app.jar \;

# Expose the port the app runs on
EXPOSE 8761

# Install wget for health check
RUN apt-get update && apt-get install -y wget

# Run the jar file
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]