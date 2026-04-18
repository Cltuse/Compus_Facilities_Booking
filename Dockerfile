FROM node:22-alpine AS frontend-build
WORKDIR /workspace/frontend

COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build


FROM maven:3.9.9-eclipse-temurin-21 AS backend-build
WORKDIR /workspace

COPY backend/ ./backend/
COPY --from=frontend-build /workspace/frontend/dist/ ./backend/src/main/resources/static/

RUN mvn -f backend/pom.xml clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=80
ENV FILE_UPLOAD_DIR=/app/files

COPY --from=backend-build /workspace/backend/target/facility-management-1.0.0.jar ./app.jar
COPY files/ ./files/

EXPOSE 80

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
