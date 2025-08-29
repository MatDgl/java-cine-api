#!/bin/bash

echo "🎬 Démarrage de Java Cine API"
echo "=============================="

# Vérifier que Java est installé
if ! command -v java &> /dev/null; then
    echo "❌ Java n'est pas installé. Veuillez installer Java 17 ou plus récent."
    exit 1
fi

# Vérifier la version de Java
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$java_version" -lt 17 ]; then
    echo "❌ Java 17 ou plus récent est requis. Version actuelle: $java_version"
    exit 1
fi

echo "✅ Java $java_version détecté"

# Vérifier que PostgreSQL est disponible
if ! command -v psql &> /dev/null; then
    echo "⚠️  PostgreSQL n'est pas installé ou pas dans le PATH."
    echo "   Assurez-vous que PostgreSQL est installé et configuré."
fi

# Build du projet
echo "🔨 Build du projet..."
./gradlew build -q

if [ $? -eq 0 ]; then
    echo "✅ Build réussi"
else
    echo "❌ Échec du build"
    exit 1
fi

# Lancement de l'application
echo "🚀 Lancement de l'application sur le port 8080..."
echo ""

./gradlew bootRun --args='--spring.profiles.active=dev'
