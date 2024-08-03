FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY user-service/target/user-service-0.0.1-SNAPSHOT.jar /app/user-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]