#!/bin/bash

# ==============================================================================
# Script de configuration initiale du VPS
# A n'exécuter qu'une seule fois !
# ==============================================================================

VPS_IP="84.247.172.198"
VPS_USER="merveille"


#if [ "$VPS_IP" = "84.247.172.198" ]; then
#    echo "❌ Erreur : Veuillez définir l'adresse IP du VPS (VPS_IP) dans ce script."
#    exit 1
#fi

echo "⚙️  1. Initialisation de l'arborescence sur le VPS..."
ssh $VPS_USER@$VPS_IP "mkdir -p /home/merveille/lawconnect-backend"

if [ -f "./.env" ]; then
    echo "📄 2. Envoi du fichier .env..."
    scp ./.env $VPS_USER@$VPS_IP:/home/merveille/lawconnect-backend/
else
    echo "⚠️  Aucun fichier .env local trouvé dans le dossier 'deploy'."
    echo "    N'oubliez pas de le créer sur le VPS à partir de .env.example."
fi

echo "📄 3. Envoi des fichiers de configuration système..."
scp ./nginx.conf $VPS_USER@$VPS_IP:/etc/nginx/sites-available/lawconnect-backend
scp ./lawconnect-backend.service $VPS_USER@$VPS_IP:/etc/systemd/system/

echo "🔄 4. Application des configurations sur le VPS..."
ssh $VPS_USER@$VPS_IP "ln -sf /etc/nginx/sites-available/lawconnect-backend /etc/nginx/sites-enabled/ && \
rm -f /etc/nginx/sites-enabled/default && \
systemctl daemon-reload && \
systemctl enable lawconnect-backend && \
systemctl restart nginx"

echo "✅ Configuration initiale terminée !"
