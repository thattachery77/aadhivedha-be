# ---------- Stage 1: Build Angular ----------
FROM node:18 AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build --prod


# ---------- Stage 2: Build Spring Boot ----------
FROM gradle:8.2.1-jdk17 AS backend-build
WORKDIR /app
COPY --chown=gradle:gradle . .

# Copy Angular build into Spring Boot static folder
RUN rm -rf src/main/resources/static/* && \
    cp -r frontend/dist/** src/main/resources/static/ || true

# Build Spring Boot (skip tests if needed)
RUN chmod +x gradlew && ./gradlew clean build -x test --no-daemon --stacktrace


# ---------- Stage 3: Runtime ----------
FROM eclipse-temurin:21-jdk AS runtime
WORKDIR /app
COPY --from=backend-build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
