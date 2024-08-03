FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY config-server/target/config-server-0.0.1-SNAPSHOT.jar /app/config-server.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "/app/config-server.jar"]