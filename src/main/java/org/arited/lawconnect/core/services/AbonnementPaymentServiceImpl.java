package org.arited.lawconnect.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arited.lawconnect.core.dtos.Request.AbonnementCheckoutRequest;
import org.arited.lawconnect.core.dtos.Response.AbonnementCheckoutResponse;
import org.arited.lawconnect.core.entities.Abonnement;
import org.arited.lawconnect.core.entities.Avocat;
import org.arited.lawconnect.core.enums.StatutPaiementEnum;
import org.arited.lawconnect.core.exceptions.AppException;
import org.arited.lawconnect.core.repositories.AbonnementRepository;
import org.arited.lawconnect.core.repositories.AvocatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AbonnementPaymentServiceImpl implements AbonnementPaymentService {

    private final AbonnementRepository abonnementRepository;
    private final AvocatRepository avocatRepository;
    private final NotchPayService notchPayService;

    @Override
    @Transactional
    public AbonnementCheckoutResponse checkout(AbonnementCheckoutRequest request) {
        log.info("Démarrage du checkout NotchPay pour l'avocat ID: {}", request.getAvocatId());

        Avocat avocat = avocatRepository.findById(request.getAvocatId())
                .orElseThrow(() -> new AppException("Avocat non trouvé avec l'ID: " + request.getAvocatId(), HttpStatus.NOT_FOUND));

        // Empêcher la création si un abonnement PAYE est déjà actif
        abonnementRepository.findByAvocatUserIdAndStatut(request.getAvocatId(), StatutPaiementEnum.PAYE)
                .ifPresent(existing -> {
                    throw new AppException("Cet avocat a déjà un abonnement actif (PAYE).", HttpStatus.CONFLICT);
                });

        // 1. Créer l'abonnement en EN_ATTENTE
        String ourReference = generateReference();
        Abonnement abonnement = new Abonnement();
        abonnement.setReference(ourReference);
        abonnement.setFormule(request.getFormule());
        abonnement.setCycle(request.getCycle());
        abonnement.setMontant(request.getMontant());
        abonnement.setStatut(StatutPaiementEnum.EN_ATTENTE);
        abonnement.setActive(true);
        abonnement.setAvocat(avocat);

        int dureeMois = request.getCycle() == org.arited.lawconnect.core.enums.CycleEnum.ANNUEL ? 12 : 1;
        abonnement.setProchainRenouvellement(LocalDate.now().plusMonths(dureeMois));

        // 2. Initialiser le paiement chez NotchPay
        String notchpayReference = notchPayService.initializePayment(
                ourReference,
                request.getMontant(),
                avocat.getEmail(),
                avocat.getFullName(),
                "Abonnement LawConnect - " + request.getFormule(),
                request.getPhone()
        );
        abonnement.setNotchpayReference(notchpayReference);

        Abonnement savedAbonnement = abonnementRepository.save(abonnement);

        // 3. Déclencher la charge directe (USSD)
        Map<?, ?> chargeResult = notchPayService.chargeDirectPayment(
                notchpayReference,
                request.getChannel(),
                request.getPhone()
        );

        log.info("Checkout initié avec succès pour l'abonnement {}", ourReference);

        return AbonnementCheckoutResponse.builder()
                .status("PENDING")
                .message("Paiement initié. Veuillez valider le prompt USSD reçu sur votre téléphone.")
                .abonnementReference(savedAbonnement.getReference())
                .notchpayReference(notchpayReference)
                .notchpayDetails(chargeResult)
                .build();
    }

    private String generateReference() {
        String reference;
        do {
            reference = "ABN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (abonnementRepository.existsByReference(reference));
        return reference;
    }
}
