FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY gateway/target/gateway-0.0.1-SNAPSHOT.jar /app/gateway.jar

EXPOSE 9091

ENTRYPOINT ["java", "-jar", "/app/gateway.jar"]