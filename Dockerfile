# ---------- Stage 1: Runtime only ----------
FROM eclipse-temurin:21-jdk AS runtime
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
