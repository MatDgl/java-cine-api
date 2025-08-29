# Dockerfile pour Java Cine API
FROM openjdk:17-jdk-slim

LABEL maintainer="Java Cine API"
LABEL description="API Spring Boot pour la gestion de films et séries avec intégration TMDB"

# Variables d'environnement
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# Répertoire de travail
WORKDIR /app

# Copier les fichiers de build Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Rendre gradlew exécutable
RUN chmod +x ./gradlew

# Copier le code source
COPY src src

# Build de l'application
RUN ./gradlew build -x test

# Créer un utilisateur non-root pour la sécurité
RUN groupadd -r spring && useradd -r -g spring spring

# Copier le JAR buildé
RUN cp build/libs/*.jar app.jar

# Changer le propriétaire
RUN chown -R spring:spring /app

# Utiliser l'utilisateur non-root
USER spring

# Exposer le port
EXPOSE $SERVER_PORT

# Point d'entrée avec profil de santé
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:$SERVER_PORT/health || exit 1

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]
