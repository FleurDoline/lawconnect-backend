package org.arited.lawconnect.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arited.lawconnect.core.dtos.Request.AvisCreateRequest;
import org.arited.lawconnect.core.dtos.Response.AvisResponse;
import org.arited.lawconnect.core.dtos.Response.NoteMoyenneResponse;
import org.arited.lawconnect.core.services.AvisService;
import org.arited.lawconnect.security.models.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/avis")
@RequiredArgsConstructor
@Tag(name = "Avis", description = "Gestion des avis clients sur les consultations")
public class AvisController {

    private final AvisService avisService;

    @PostMapping
    @Operation(summary = "Laisser un avis sur une consultation terminée")
    public ResponseEntity<AvisResponse> creerAvis(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AvisCreateRequest request) {
        log.info("POST /api/v1/avis - clientId={} consultationId={}", principal.getId(), request.getConsultationId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(avisService.creerAvis(principal.getId(), request));
    }

    @GetMapping("/avocat/{avocatUserId}")
    @Operation(summary = "Lister les avis reçus par un avocat")
    public ResponseEntity<List<AvisResponse>> getAvisPourAvocat(@PathVariable Long avocatUserId) {
        log.info("GET /api/v1/avis/avocat/{}", avocatUserId);
        return ResponseEntity.ok(avisService.getAvisPourAvocat(avocatUserId));
    }

    @GetMapping("/avocat/{avocatUserId}/moyenne")
    @Operation(summary = "Récupérer la note moyenne d'un avocat")
    public ResponseEntity<NoteMoyenneResponse> getNoteMoyenne(@PathVariable Long avocatUserId) {
        log.info("GET /api/v1/avis/avocat/{}/moyenne", avocatUserId);
        return ResponseEntity.ok(avisService.getNoteMoyenne(avocatUserId));
    }
}