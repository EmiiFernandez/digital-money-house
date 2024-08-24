FROM openjdk:17

WORKDIR /app

COPY config-server/target/config-server-0.0.1-SNAPSHOT.jar /app/config-server.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "/app/config-server.jar"]