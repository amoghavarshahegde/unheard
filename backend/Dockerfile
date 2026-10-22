FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/agentguard-backend-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-jar", "app.jar"]
