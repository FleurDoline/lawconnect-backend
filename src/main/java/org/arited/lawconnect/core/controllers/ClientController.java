package org.arited.lawconnect.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.arited.lawconnect.core.dtos.Pagination.PageResponse;
import org.arited.lawconnect.core.dtos.Request.ClientRequest;
import org.arited.lawconnect.core.dtos.Response.ClientResponse;
import org.arited.lawconnect.core.services.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Gestion des profils clients")
public class ClientController {

    private final ClientService clientService;

    // Called by auth-service via OpenFeign after account creation
    @PostMapping
    @Operation(summary = "Créer un profil client", description = "Appelé par auth-service via OpenFeign")
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientRequest request) {
        log.info("POST /api/v1/clients - userId={}", request.getUserId());
        ClientResponse response = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un client par ID interne")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long id) {
        log.info("GET /api/v1/clients/{}", id);
        return ResponseEntity.ok(clientService.getClientByUserId(id));
    }

    // Used by auth-service or API Gateway to fetch profile by auth userId
    @GetMapping("/by-user-id/{userId}")
    @Operation(summary = "Récupérer un client par userId auth-service")
    public ResponseEntity<ClientResponse> getClientByUserId(@PathVariable Long userId) {
        log.info("GET /api/v1/clients/by-user-id/{}", userId);
        return ResponseEntity.ok(clientService.getClientByUserId(userId));
    }

    @GetMapping
    @Operation(summary = "Lister tous les clients avec pagination")
    public ResponseEntity<PageResponse<ClientResponse>> getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/v1/clients?page={}&size={}", page, size);
        return ResponseEntity.ok(clientService.getAllClients(page, size));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un profil client")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request) {
        log.info("PUT /api/v1/clients/{}", id);
        return ResponseEntity.ok(clientService.updateClient(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un profil client")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        log.info("DELETE /api/v1/clients/{}", id);
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}