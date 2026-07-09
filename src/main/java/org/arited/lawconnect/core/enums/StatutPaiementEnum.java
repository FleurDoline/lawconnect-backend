package org.arited.lawconnect.core.enums;

public enum StatutPaiementEnum {
    EN_ATTENTE,    // Paiement initié mais non confirmé
    PAYE,          // Paiement confirmé et abonnement actif
    EXPIRE,        // Abonnement expiré
    ANNULE,        // Abonnement annulé par l'avocat ou l'admin
    ECHOUE         // Échec du paiement
}