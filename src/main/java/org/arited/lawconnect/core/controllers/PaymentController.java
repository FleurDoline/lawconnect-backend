package org.arited.lawconnect.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.arited.lawconnect.core.dtos.Request.AbonnementCheckoutRequest;
import org.arited.lawconnect.core.dtos.Response.AbonnementCheckoutResponse;
import org.arited.lawconnect.core.services.AbonnementPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/abonnements/checkout")
@RequiredArgsConstructor
@Tag(name = "Paiement Abonnement", description = "Checkout NotchPay pour les abonnements avocats")
public class PaymentController {

    private final AbonnementPaymentService abonnementPaymentService;

    @PostMapping
    @PreAuthorize("hasRole('AVOCAT')")
    @Operation(summary = "Initier le paiement d'un abonnement via NotchPay")
    public ResponseEntity<AbonnementCheckoutResponse> checkout(
            @Valid @RequestBody AbonnementCheckoutRequest request) {
        return ResponseEntity.ok(abonnementPaymentService.checkout(request));
    }
}