package org.arited.lawconnect.core.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.arited.lawconnect.core.dtos.Request.AbonnementRequest;
import org.arited.lawconnect.core.dtos.Response.AbonnementResponse;
import org.arited.lawconnect.core.enums.FormuleEnum;
import org.arited.lawconnect.core.enums.StatutPaiementEnum;

public interface AbonnementService {

    /**
     * Crée un nouvel abonnement
     */
    AbonnementResponse createAbonnement(AbonnementRequest request);

    /**
     * Récupère un abonnement par sa référence
     */
    AbonnementResponse getAbonnementByReference(String reference);

    /**
     * Récupère tous les abonnements d'un avocat
     */
    List<AbonnementResponse> getAbonnementsByAvocat(Long avocatId);

    /**
     * Récupère l'abonnement actif d'un avocat (statut PAYE)
     */
    AbonnementResponse getActiveAbonnementByAvocat(Long avocatId);

    /**
     * Récupère les abonnements par statut de paiement
     */
    List<AbonnementResponse> getAbonnementsByStatut(StatutPaiementEnum statut);

    /**
     * Récupère les abonnements par formule
     */
    List<AbonnementResponse> getAbonnementsByFormule(FormuleEnum formule);

    /**
     * Récupère les abonnements qui expirent avant une date donnée
     */
    List<AbonnementResponse> getAbonnementsExpiringBefore(LocalDate date);

    /**
     * Met à jour le statut de paiement d'un abonnement
     */
    AbonnementResponse updateAbonnementStatut(String reference, StatutPaiementEnum statut);

    /**
     * Met à jour un abonnement
     */
    AbonnementResponse updateAbonnement(String reference, AbonnementRequest request);

    /**
     * Renouvelle un abonnement
     */
    AbonnementResponse renewAbonnement(String reference);

    /**
     * Supprime logiquement un abonnement
     */
    void deleteAbonnement(String reference);

    /**
     * Récupère les statistiques des abonnements
     */
    Map<String, Object> getAbonnementStats();
}