# ---------- Stage 1: Build Angular + Spring Boot ----------
FROM node:18 AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ .
RUN npm run build --prod


FROM gradle:8.2.1-jdk17 as builder
# or
FROM openjdk:17-jdk-slim

#FROM gradle:8.5-jdk21 AS backend-build
#WORKDIR /app
#COPY --chown=gradle:gradle . .

# copy angular dist into static folder
# RUN rm -rf src/main/resources/static/* && \
#  cp -r frontend/dist/* src/main/resources/static/
RUN 	chmod +x gradlew && ./gradlew clean build  --no-daemon

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jdk AS runtime
WORKDIR /app
COPY --from=backend-build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
