#!/bin/bash

echo "🧪 Tests Java Cine API"
echo "======================"

# Tests unitaires
echo "▶️  Lancement des tests unitaires..."
./gradlew test

if [ $? -eq 0 ]; then
    echo "✅ Tests unitaires réussis"
else
    echo "❌ Échec des tests unitaires"
    exit 1
fi

# # Tests d'intégration (optionnel)
# echo ""
# echo "▶️  Tests d'intégration..."
# ./gradlew integrationTest 2>/dev/null || echo "ℹ️  Pas de tests d'intégration configurés"

# # Rapport de couverture (optionnel)
# echo ""
# echo "📊 Génération du rapport de couverture..."
# ./gradlew jacocoTestReport 2>/dev/null || echo "ℹ️  JaCoCo non configuré"

echo ""
echo "✅ Tests terminés avec succès!"
echo "📂 Rapports disponibles dans: build/reports/"
