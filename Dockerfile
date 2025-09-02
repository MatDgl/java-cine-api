# FROM openjdk:17-jdk-slim
# WORKDIR /app
# COPY . .
# RUN ./gradlew build
# EXPOSE 8080
# RUN cp build/libs/*.jar app.jar
# ENTRYPOINT ["java", "-jar", "app.jar"]

# Build local puis copie des fichiers (problème proxy)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
