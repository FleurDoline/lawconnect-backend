package org.arited.lawconnect.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.arited.lawconnect.core.dtos.Request.AbonnementRequest;
import org.arited.lawconnect.core.dtos.Response.AbonnementResponse;
import org.arited.lawconnect.core.entities.Abonnement;
import org.arited.lawconnect.core.entities.Avocat;
import org.arited.lawconnect.core.enums.CycleEnum;
import org.arited.lawconnect.core.enums.StatutPaiementEnum;
import org.arited.lawconnect.core.exceptions.AppException;
import org.arited.lawconnect.core.mappers.AbonnementMapper;
import org.arited.lawconnect.core.repositories.AbonnementRepository;
import org.arited.lawconnect.core.repositories.AvocatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AbonnementServiceImpl implements AbonnementService {

    private final AbonnementRepository abonnementRepository;
    private final AvocatRepository avocatRepository;
    private final AbonnementMapper abonnementMapper;
    private final AvocatService avocatService;

    @Override
    @Transactional
    public AbonnementResponse createAbonnement(AbonnementRequest request) {
        log.info("Création d'un nouvel abonnement pour l'avocat ID: {}", request.getAvocatId());

        // Vérifier que l'avocat existe
        Avocat avocat = avocatRepository.findById(request.getAvocatId())
                .orElseThrow(() -> new AppException("Avocat non trouvé avec l'ID: " + request.getAvocatId(), HttpStatus.NOT_FOUND));

        // Générer une référence si non fournie, sinon vérifier son unicité
        if (request.getReference() == null || request.getReference().isBlank()) {
            request.setReference(generateReference());
        } else if (abonnementRepository.existsByReference(request.getReference())) {
            throw new AppException("La référence '" + request.getReference() + "' existe déjà", HttpStatus.CONFLICT);
        }

        // Vérifier si l'avocat a déjà un abonnement actif (PAYE)
        abonnementRepository.findByAvocatUserIdAndStatut(request.getAvocatId(), StatutPaiementEnum.PAYE)
                .ifPresent(existing -> {
                    throw new AppException("Cet avocat a déjà un abonnement actif (PAYE). Veuillez le renouveler ou l'annuler d'abord.", HttpStatus.CONFLICT);
                });

        // Créer le nouvel abonnement
        Abonnement abonnement = abonnementMapper.toEntity(request);
        abonnement.setAvocat(avocat);
        abonnement.setStatut(StatutPaiementEnum.EN_ATTENTE);
        abonnement.setActive(true);

        // Calculer la date de prochain renouvellement si non fournie
        if (request.getProchainRenouvellement() == null) {
            LocalDate now = LocalDate.now();
            int dureeMois = getDureeMois(request.getCycle());
            abonnement.setProchainRenouvellement(now.plusMonths(dureeMois));
        }

        Abonnement savedAbonnement = abonnementRepository.save(abonnement);
        log.info("Abonnement créé avec succès, référence: {}", savedAbonnement.getReference());

        return abonnementMapper.toResponse(savedAbonnement);
    }

    @Override
    public AbonnementResponse getAbonnementByReference(String reference) {
        log.debug("Récupération de l'abonnement par référence: {}", reference);

        Abonnement abonnement = abonnementRepository.findByReference(reference)
                .orElseThrow(() -> new AppException("Abonnement non trouvé avec la référence: " + reference, HttpStatus.NOT_FOUND));

        return abonnementMapper.toResponse(abonnement);
    }

    @Override
    public List<AbonnementResponse> getAbonnementsByAvocat(Long avocatId) {
        log.debug("Récupération des abonnements de l'avocat ID: {}", avocatId);

        // Vérifier que l'avocat existe
        if (!avocatRepository.existsById(avocatId)) {
            throw new AppException("Avocat non trouvé avec l'ID: " + avocatId, HttpStatus.NOT_FOUND);
        }

        List<Abonnement> abonnements = abonnementRepository.findByAvocatUserId(avocatId);
        return abonnements.stream()
                .map(abonnementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AbonnementResponse getActiveAbonnementByAvocat(Long avocatId) {
        log.debug("Récupération de l'abonnement actif de l'avocat ID: {}", avocatId);

        // Vérifier que l'avocat existe
        if (!avocatRepository.existsById(avocatId)) {
            throw new AppException("Avocat non trouvé avec l'ID: " + avocatId, HttpStatus.NOT_FOUND);
        }

        Abonnement abonnement = abonnementRepository.findByAvocatUserIdAndStatut(avocatId, StatutPaiementEnum.PAYE)
                .orElseThrow(() -> new AppException("Aucun abonnement actif (PAYE) trouvé pour cet avocat", HttpStatus.NOT_FOUND));

        return abonnementMapper.toResponse(abonnement);
    }

    @Override
    public List<AbonnementResponse> getAbonnementsByStatut(StatutPaiementEnum statut) {
        log.debug("Récupération des abonnements par statut: {}", statut);

        List<Abonnement> abonnements = abonnementRepository.findByStatut(statut);
        return abonnements.stream()
                .map(abonnementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AbonnementResponse> getAbonnementsByFormule(org.arited.lawconnect.core.enums.FormuleEnum formule) {
        log.debug("Récupération des abonnements par formule: {}", formule);

        List<Abonnement> abonnements = abonnementRepository.findByFormule(formule);
        return abonnements.stream()
                .map(abonnementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AbonnementResponse> getAbonnementsExpiringBefore(LocalDate date) {
        log.debug("Récupération des abonnements expirant avant: {}", date);

        List<Abonnement> abonnements = abonnementRepository.findByProchainRenouvellementBefore(date);
        return abonnements.stream()
                .map(abonnementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AbonnementResponse updateAbonnementStatut(String reference, StatutPaiementEnum statut) {
        log.info("Mise à jour du statut de l'abonnement {} vers {}", reference, statut);

        Abonnement abonnement = abonnementRepository.findByReference(reference)
                .orElseThrow(() -> new AppException("Abonnement non trouvé avec la référence: " + reference, HttpStatus.NOT_FOUND));

        abonnement.setStatut(statut);
        
        // Si le statut devient PAYE, mettre à jour la date de prochain renouvellement
        if (statut == StatutPaiementEnum.PAYE) {
            LocalDate now = LocalDate.now();
            int dureeMois = getDureeMois(abonnement.getCycle());
            abonnement.setProchainRenouvellement(now.plusMonths(dureeMois));
            abonnement.setActive(true);
        }

        Abonnement updatedAbonnement = abonnementRepository.save(abonnement);

        // Le statut PAYE / non-PAYE influence le critère "abonnement actif" de la progression du profil
        avocatService.recalculerProgression(updatedAbonnement.getAvocat().getUserId());

        log.info("Statut de l'abonnement {} mis à jour avec succès", reference);

        return abonnementMapper.toResponse(updatedAbonnement);
    }

    @Override
    @Transactional
    public AbonnementResponse updateAbonnement(String reference, AbonnementRequest request) {
        log.info("Mise à jour de l'abonnement: {}", reference);

        Abonnement existingAbonnement = abonnementRepository.findByReference(reference)
                .orElseThrow(() -> new AppException("Abonnement non trouvé avec la référence: " + reference, HttpStatus.NOT_FOUND));

        // Vérifier que la nouvelle référence n'est pas déjà utilisée par un autre abonnement
        if (!existingAbonnement.getReference().equals(request.getReference()) &&
                abonnementRepository.existsByReference(request.getReference())) {
            throw new AppException("La référence '" + request.getReference() + "' existe déjà", HttpStatus.CONFLICT);
        }

        // Mettre à jour les champs
        abonnementMapper.updateEntity(request, existingAbonnement);

        // Si la date de prochain renouvellement n'est pas fournie, la calculer
        if (request.getProchainRenouvellement() == null) {
            LocalDate now = LocalDate.now();
            int dureeMois = getDureeMois(request.getCycle());
            existingAbonnement.setProchainRenouvellement(now.plusMonths(dureeMois));
        }

        Abonnement updatedAbonnement = abonnementRepository.save(existingAbonnement);
        log.info("Abonnement {} mis à jour avec succès", reference);

        return abonnementMapper.toResponse(updatedAbonnement);
    }

    @Override
    @Transactional
    public AbonnementResponse renewAbonnement(String reference) {
        log.info("Renouvellement de l'abonnement: {}", reference);

        Abonnement existingAbonnement = abonnementRepository.findByReference(reference)
                .orElseThrow(() -> new AppException("Abonnement non trouvé avec la référence: " + reference, HttpStatus.NOT_FOUND));

        // Vérifier que l'abonnement n'est pas expiré (si la date est dépassée)
        if (existingAbonnement.getProchainRenouvellement().isBefore(LocalDate.now())) {
            throw new AppException("L'abonnement est expiré depuis le " + existingAbonnement.getProchainRenouvellement() + 
                    ". Veuillez en créer un nouveau.", HttpStatus.BAD_REQUEST);
        }

        // Vérifier que l'abonnement est en statut PAYE
        if (existingAbonnement.getStatut() != StatutPaiementEnum.PAYE) {
            throw new AppException("Seul un abonnement en statut PAYE peut être renouvelé", HttpStatus.BAD_REQUEST);
        }

        // Mettre à jour la date de prochain renouvellement
        int dureeMois = getDureeMois(existingAbonnement.getCycle());
        LocalDate newRenewalDate = existingAbonnement.getProchainRenouvellement().plusMonths(dureeMois);
        existingAbonnement.setProchainRenouvellement(newRenewalDate);
        
        // Générer une nouvelle référence pour le suivi
        String newReference = "RENEW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        existingAbonnement.setReference(newReference);

        Abonnement renewedAbonnement = abonnementRepository.save(existingAbonnement);
        log.info("Abonnement renouvelé avec succès, nouvelle référence: {}", newReference);

        return abonnementMapper.toResponse(renewedAbonnement);
    }

    @Override
    @Transactional
    public void deleteAbonnement(String reference) {
        log.info("Suppression logique de l'abonnement: {}", reference);

        Abonnement abonnement = abonnementRepository.findByReference(reference)
                .orElseThrow(() -> new AppException("Abonnement non trouvé avec la référence: " + reference, HttpStatus.NOT_FOUND));

        abonnement.setActive(false);
        abonnement.setDeletedAt(Instant.now());
        abonnementRepository.save(abonnement);

        // Peut faire retomber le critère "abonnement actif" si c'était le PAYE en cours
        avocatService.recalculerProgression(abonnement.getAvocat().getUserId());

        log.info("Abonnement {} supprimé logiquement avec succès", reference);
    }

    @Override
    public Map<String, Object> getAbonnementStats() {
        log.debug("Récupération des statistiques des abonnements");

        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques générales
        stats.put("totalAbonnements", abonnementRepository.count());
        stats.put("abonnementsActifs", abonnementRepository.findByStatut(StatutPaiementEnum.PAYE).size());
        stats.put("abonnementsEnAttente", abonnementRepository.findByStatut(StatutPaiementEnum.EN_ATTENTE).size());
        stats.put("abonnementsEchec", abonnementRepository.findByStatut(StatutPaiementEnum.ECHOUE).size());
        
        // Statistiques par formule
        Map<String, Long> statsParFormule = new HashMap<>();
        for (org.arited.lawconnect.core.enums.FormuleEnum formule : org.arited.lawconnect.core.enums.FormuleEnum.values()) {
            long count = abonnementRepository.findByFormule(formule).size();
            statsParFormule.put(formule.name(), count);
        }
        stats.put("statsParFormule", statsParFormule);
        
        // Statistiques par cycle
        Map<String, Long> statsParCycle = new HashMap<>();
        for (CycleEnum cycle : CycleEnum.values()) {
            long count = abonnementRepository.findAll().stream()
                    .filter(a -> a.getCycle() == cycle)
                    .count();
            statsParCycle.put(cycle.name(), count);
        }
        stats.put("statsParCycle", statsParCycle);
        
        return stats;
    }

    /**
     * Helper method to get duration in months from CycleEnum
     */
    private int getDureeMois(CycleEnum cycle) {
        if (cycle == null) {
            return 1; // Default to 1 month
        }
        return switch (cycle) {
            case MENSUEL -> 1;
            case TRIMESTRIEL -> 3;
            case ANNUEL -> 12;
        };
    }

    /**
     * Génère une référence unique pour un nouvel abonnement (ex: ABN-4F9C21A0)
     */
    private String generateReference() {
        String reference;
        do {
            reference = "ABN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (abonnementRepository.existsByReference(reference));
        return reference;
    }
}