package org.arited.lawconnect.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arited.lawconnect.core.dtos.Pagination.PageResponse;
import org.arited.lawconnect.core.dtos.Request.AdminCreateRequest;
import org.arited.lawconnect.core.dtos.Request.AdminUpdateRequest;
import org.arited.lawconnect.core.dtos.Response.AdminResponse;
import org.arited.lawconnect.core.enums.AccesEnum;
import org.arited.lawconnect.core.services.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
@Tag(name = "Admins", description = "Gestion des comptes administrateurs")
@PreAuthorize("hasRole('ADMIN')") // every endpoint here requires an existing admin
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    @Operation(summary = "Créer un compte admin", description = "Réservé aux administrateurs existants")
    public ResponseEntity<AdminResponse> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        log.info("POST /api/v1/admins - email={}", request.getEmail());
        AdminResponse response = adminService.createAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un admin par ID interne")
    public ResponseEntity<AdminResponse> getAdminById(@PathVariable Long id) {
        log.info("GET /api/v1/admins/{}", id);
        return ResponseEntity.ok(adminService.getAdminById(id));
    }

    @GetMapping("/by-user-id/{userId}")
    @Operation(summary = "Récupérer un admin par userId")
    public ResponseEntity<AdminResponse> getAdminByUserId(@PathVariable Long userId) {
        log.info("GET /api/v1/admins/by-user-id/{}", userId);
        return ResponseEntity.ok(adminService.getAdminByUserId(userId));
    }

    @GetMapping
    @Operation(summary = "Lister tous les admins avec pagination")
    public ResponseEntity<PageResponse<AdminResponse>> getAllAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/v1/admins?page={}&size={}", page, size);
        return ResponseEntity.ok(adminService.getAllAdmins(page, size));
    }

    @GetMapping("/niveau-acces/{niveauAcces}")
    @Operation(summary = "Filtrer les admins par niveau d'accès")
    public ResponseEntity<List<AdminResponse>> getAdminsByNiveauAcces(@PathVariable AccesEnum niveauAcces) {
        log.info("GET /api/v1/admins/niveau-acces/{}", niveauAcces);
        return ResponseEntity.ok(adminService.getAdminsByNiveauAcces(niveauAcces));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un profil admin")
    public ResponseEntity<AdminResponse> updateAdmin(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateRequest request) {
        log.info("PUT /api/v1/admins/{}", id);
        return ResponseEntity.ok(adminService.updateAdmin(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un compte admin")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        log.info("DELETE /api/v1/admins/{}", id);
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
