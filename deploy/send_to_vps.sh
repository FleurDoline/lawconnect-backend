#!/bin/bash

# ==============================================================================
# Script de déploiement continu vers le VPS
# ==============================================================================

VPS_IP=84.247.172.198
VPS_USER="merveille"
VPS_DIR="/home/merveille/lawconnect-backend"
JAR_NAME="lawconnect-0.0.1-SNAPSHOT.jar"



echo "🔨 1. Compilation du projet backend (sans les tests)..."
# On se place à la racine du projet (le dossier parent de 'deploy')
cd ..
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de la compilation."
    exit 1
fi

echo "🚀 2. Envoi du fichier .jar vers le VPS..."
scp target/$JAR_NAME $VPS_USER@$VPS_IP:$VPS_DIR/
if [ $? -ne 0 ]; then
    echo "❌ Erreur lors de l'envoi du fichier vers le VPS."
    exit 1
fi

echo "🔄 3. Redémarrage du service sur le VPS..."
ssh $VPS_USER@$VPS_IP "systemctl restart lawconnect-backend"

echo "✅ Déploiement terminé avec succès !"
