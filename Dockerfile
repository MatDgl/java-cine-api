FROM openjdk:17-jdk-slim

WORKDIR /app

COPY . .

RUN ./gradlew build

EXPOSE 8080

RUN cp build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
