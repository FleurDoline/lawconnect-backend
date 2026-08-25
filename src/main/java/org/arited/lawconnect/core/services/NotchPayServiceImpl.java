package org.arited.lawconnect.core.services;

import org.arited.lawconnect.core.configs.NotchPayProperties;
import org.arited.lawconnect.core.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotchPayServiceImpl implements NotchPayService {

    private final RestClient notchPayRestClient;
    private final NotchPayProperties notchPayProperties;

    @Override
    public String initializePayment(String reference, BigDecimal amount, String email, String customerName, String description, String phone) {
        log.info("Initialisation du paiement NotchPay. Ref: {}", reference);

        Map<String, Object> body = Map.of(
                "amount", amount,
                "currency", notchPayProperties.getCurrency(),
                "reference", reference,
                "description", description,
                "customer", Map.of(
                        "name", customerName,
                        "email", email,
                        "phone", phone
                ),
                "callback_url", notchPayProperties.getCallbackUrl()

        );

        try {
            Map<?, ?> response = notchPayRestClient.post()
                    .uri("/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("transaction")) {
                Map<?, ?> transaction = (Map<?, ?>) response.get("transaction");
                return (String) transaction.get("reference");
            }
            throw new AppException("Réponse invalide reçue de NotchPay lors de l'initialisation", HttpStatus.BAD_GATEWAY);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation de la transaction NotchPay", e);
            throw new AppException("Impossible d'initialiser la transaction : " + e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    @Override
    public Map<?, ?> chargeDirectPayment(String notchpayReference, String channel, String phone) {
        log.info("Déclenchement de la charge directe. Ref: {}, Channel: {}", notchpayReference, channel);

        Map<String, Object> body = Map.of(
                "channel", channel,
                "data", Map.of("phone", phone)
        );

        try {
            return notchPayRestClient.post()
                    .uri("/payments/{reference}", notchpayReference)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            log.error("Erreur lors du traitement du paiement direct NotchPay", e);
            throw new AppException("Échec du déclenchement du paiement direct : " + e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}