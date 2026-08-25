package org.arited.lawconnect.core.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arited.lawconnect.core.configs.NotchPayProperties;
import org.arited.lawconnect.core.entities.Abonnement;
import org.arited.lawconnect.core.enums.StatutPaiementEnum;
import org.arited.lawconnect.core.repositories.AbonnementRepository;
import org.arited.lawconnect.core.services.AvocatService;
import org.arited.lawconnect.security.utils.HmacSignatureValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final NotchPayProperties notchPayProperties;
    private final AbonnementRepository abonnementRepository;
    private final AvocatService avocatService;
    private final ObjectMapper objectMapper;

    @PostMapping("/notchpay")
    public ResponseEntity<String> handleNotchPayWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Notch-Signature", required = false) String signature) {

        log.info("Réception d'une notification Webhook NotchPay");

        boolean isValid = HmacSignatureValidator.isValidSignature(
                payload, signature, notchPayProperties.getHashKey());

        if (!isValid) {
            log.warn("Signature Webhook invalide - requête rejetée");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Signature Invalide");
        }

        try {
            JsonNode root = objectMapper.readTree(payload);
            String event = root.path("event").asText();
            JsonNode data = root.path("data");
            String notchpayReference = data.path("reference").asText(null);

            if (notchpayReference == null) {
                log.warn("Webhook reçu sans référence de transaction, ignoré");
                return ResponseEntity.ok("Ignoré - pas de référence");
            }

            Abonnement abonnement = abonnementRepository.findByNotchpayReference(notchpayReference)
                    .orElse(null);

            if (abonnement == null) {
                log.warn("Aucun abonnement trouvé pour la référence NotchPay: {}", notchpayReference);
                return ResponseEntity.ok("Ignoré - abonnement introuvable");
            }

            // Idempotence : si déjà traité, ne rien refaire
            if (abonnement.getStatut() == StatutPaiementEnum.PAYE && "payment.complete".equals(event)) {
                log.info("Abonnement {} déjà marqué PAYE, webhook ignoré", abonnement.getReference());
                return ResponseEntity.ok("Déjà traité");
            }

            if ("payment.complete".equals(event)) {
                int dureeMois = abonnement.getCycle() == org.arited.lawconnect.core.enums.CycleEnum.ANNUEL ? 12 : 1;
                abonnement.setStatut(StatutPaiementEnum.PAYE);
                abonnement.setProchainRenouvellement(LocalDate.now().plusMonths(dureeMois));
                abonnement.setActive(true);
                abonnementRepository.save(abonnement);
                avocatService.recalculerProgression(abonnement.getAvocat().getUserId());
                log.info("Abonnement {} confirmé PAYE via webhook", abonnement.getReference());

            } else if ("payment.failed".equals(event)) {
                abonnement.setStatut(StatutPaiementEnum.ECHOUE);
                abonnementRepository.save(abonnement);
                log.info("Abonnement {} marqué ECHOUE via webhook", abonnement.getReference());
            }

            return ResponseEntity.ok("Webhook Traité avec Succès");

        } catch (Exception e) {
            log.error("Erreur lors du traitement du webhook NotchPay", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur de traitement");
        }
    }
}
