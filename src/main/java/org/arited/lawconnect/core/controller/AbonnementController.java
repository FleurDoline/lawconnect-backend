package org.arited.lawconnect.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.arited.lawconnect.core.dtos.Request.AbonnementRequest;
import org.arited.lawconnect.core.dtos.Response.AbonnementResponse;
import org.arited.lawconnect.core.enums.FormuleEnum;
import org.arited.lawconnect.core.enums.StatutPaiementEnum;
import org.arited.lawconnect.core.services.AbonnementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/abonnements")
@RequiredArgsConstructor
@Tag(name = "Abonnement", description = "Endpoints de gestion des abonnements")
public class AbonnementController {

    private final AbonnementService abonnementService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AVOCAT')")
    @Operation(summary = "Créer un nouvel abonnement")
    public ResponseEntity<AbonnementResponse> createAbonnement(
            @Valid @RequestBody AbonnementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(abonnementService.createAbonnement(request));
    }

    @GetMapping("/{reference}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AVOCAT')")
    @Operation(summary = "Récupérer un abonnement par sa référence")
    public ResponseEntity<AbonnementResponse> getAbonnementByReference(
            @PathVariable String reference) {
        return ResponseEntity.ok(abonnementService.getAbonnementByReference(reference));
    }

    @GetMapping("/avocat/{avocatId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AVOCAT')")
    @Operation(summary = "Récupérer tous les abonnements d'un avocat")
    public ResponseEntity<List<AbonnementResponse>> getAbonnementsByAvocat(
            @PathVariable Long avocatId) {
        return ResponseEntity.ok(abonnementService.getAbonnementsByAvocat(avocatId));
    }

    @GetMapping("/avocat/{avocatId}/actif")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AVOCAT')")
    @Operation(summary = "Récupérer l'abonnement actif d'un avocat")
    public ResponseEntity<AbonnementResponse> getActiveAbonnementByAvocat(
            @PathVariable Long avocatId) {
        return ResponseEntity.ok(abonnementService.getActiveAbonnementByAvocat(avocatId));
    }

    @GetMapping("/statut/{statut}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupérer les abonnements par statut de paiement")
    public ResponseEntity<List<AbonnementResponse>> getAbonnementsByStatut(
            @PathVariable StatutPaiementEnum statut) {
        return ResponseEntity.ok(abonnementService.getAbonnementsByStatut(statut));
    }

    @GetMapping("/formule/{formule}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupérer les abonnements par formule")
    public ResponseEntity<List<AbonnementResponse>> getAbonnementsByFormule(
            @PathVariable FormuleEnum formule) {
        return ResponseEntity.ok(abonnementService.getAbonnementsByFormule(formule));
    }

    @GetMapping("/expirant")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupérer les abonnements qui expirent avant une date donnée")
    public ResponseEntity<List<AbonnementResponse>> getAbonnementsExpiringBefore(
            @RequestParam LocalDate date) {
        return ResponseEntity.ok(abonnementService.getAbonnementsExpiringBefore(date));
    }

    @PatchMapping("/{reference}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour le statut de paiement d'un abonnement")
    public ResponseEntity<AbonnementResponse> updateAbonnementStatut(
            @PathVariable String reference,
            @RequestParam StatutPaiementEnum statut) {
        return ResponseEntity.ok(abonnementService.updateAbonnementStatut(reference, statut));
    }

    @PutMapping("/{reference}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour un abonnement")
    public ResponseEntity<AbonnementResponse> updateAbonnement(
            @PathVariable String reference,
            @Valid @RequestBody AbonnementRequest request) {
        return ResponseEntity.ok(abonnementService.updateAbonnement(reference, request));
    }

    @PostMapping("/{reference}/renouveler")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AVOCAT')")
    @Operation(summary = "Renouveler un abonnement")
    public ResponseEntity<AbonnementResponse> renewAbonnement(
            @PathVariable String reference) {
        return ResponseEntity.ok(abonnementService.renewAbonnement(reference));
    }

    @DeleteMapping("/{reference}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer logiquement un abonnement")
    public ResponseEntity<Void> deleteAbonnement(
            @PathVariable String reference) {
        abonnementService.deleteAbonnement(reference);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Statistiques des abonnements")
    public ResponseEntity<Map<String, Object>> getAbonnementStats() {
        return ResponseEntity.ok(abonnementService.getAbonnementStats());
    }
}