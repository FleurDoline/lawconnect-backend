package org.arited.lawconnect.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arited.lawconnect.core.dtos.Request.ConsultationCreateRequest;
import org.arited.lawconnect.core.dtos.Response.ConsultationAvocatSummaryResponse;
import org.arited.lawconnect.core.dtos.Response.ConsultationDetailResponse;
import org.arited.lawconnect.core.dtos.Response.ConsultationResponse;
import org.arited.lawconnect.core.dtos.Response.ConsultationSummaryResponse;
import org.arited.lawconnect.core.services.ConsultationService;
import org.arited.lawconnect.security.models.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/consultations")
@RequiredArgsConstructor
@Tag(name = "Consultations", description = "Gestion des demandes de consultation")
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping
    @Operation(summary = "Créer une demande de consultation")
    public ResponseEntity<ConsultationResponse> createConsultation(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ConsultationCreateRequest request) {
        log.info("POST /api/v1/consultations - clientId={} avocatId={}", principal.getId(), request.getAvocatId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(consultationService.createConsultation(principal.getId(), request));
    }

    @GetMapping("/mes-consultations")
    @Operation(summary = "Lister les consultations du client connecté")
    public ResponseEntity<List<ConsultationSummaryResponse>> getMesConsultations(
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("GET /api/v1/consultations/mes-consultations - clientId={}", principal.getId());
        return ResponseEntity.ok(consultationService.getConsultationsForClient(principal.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer le détail d'une consultation")
    public ResponseEntity<ConsultationDetailResponse> getConsultationDetail(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        log.info("GET /api/v1/consultations/{} - clientId={}", id, principal.getId());
        return ResponseEntity.ok(consultationService.getConsultationDetail(principal.getId(), id));
    }

    @GetMapping("/mes-demandes")
    @Operation(summary = "Lister les demandes de consultation reçues par l'avocat connecté")
    public ResponseEntity<List<ConsultationAvocatSummaryResponse>> getMesDemandes(
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("GET /api/v1/consultations/mes-demandes - avocatUserId={}", principal.getId());
        return ResponseEntity.ok(consultationService.getConsultationsForAvocat(principal.getId()));
    }

    @PatchMapping("/{id}/accepter")
    @Operation(summary = "Accepter une demande de consultation")
    public ResponseEntity<ConsultationResponse> accepterConsultation(
          @AuthenticationPrincipal UserPrincipal principal,
          @PathVariable Long id) {
        log.info("PATCH /api/v1/consultations/{}/accepter - avocatUserId={}", id, principal.getId());
      return ResponseEntity.ok(consultationService.accepterConsultation(principal.getId(), id));
    }
}