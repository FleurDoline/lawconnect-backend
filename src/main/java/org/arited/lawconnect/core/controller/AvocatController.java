package org.arited.lawconnect.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.arited.lawconnect.core.dtos.Pagination.PageResponse;
import org.arited.lawconnect.core.dtos.Request.AvocatCreateRequest;
import org.arited.lawconnect.core.dtos.Request.AvocatUpdateRequest;
import org.arited.lawconnect.core.dtos.Response.AvocatResponse;
import org.arited.lawconnect.core.dtos.Response.AvocatSummaryResponse;
import org.arited.lawconnect.core.enums.DocumentTypeEnum;
import org.arited.lawconnect.core.enums.StatutAvocatEnum;
import org.arited.lawconnect.core.enums.TypePieceIdentiteEnum;
import org.arited.lawconnect.core.services.AvocatService;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/avocats")
@RequiredArgsConstructor
@Tag(name = "Avocats", description = "Gestion des profils avocats")
public class AvocatController {

    private final AvocatService avocatService;
    @Value("${app.upload.documents.dir:uploads/documents}")
    private String uploadDocumentsDir;

    @Value("${app.upload.dir:uploads/photos}")
    private String uploadDir;

    // Called by auth-service via OpenFeign after account creation
    @PostMapping
    @Operation(summary = "Créer un profil avocat", description = "Appelé par auth-service via OpenFeign")
    public ResponseEntity<AvocatResponse> createAvocat(@Valid @RequestBody AvocatCreateRequest request) {
        log.info("POST /api/v1/avocats - userId={}", request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(avocatService.createAvocat(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un avocat par ID interne")
    public ResponseEntity<AvocatResponse> getAvocatById(@PathVariable Long id) {
        log.info("GET /api/v1/avocats/{}", id);
        return ResponseEntity.ok(avocatService.getAvocatById(id));
    }

    @GetMapping("/by-user-id/{userId}")
    @Operation(summary = "Récupérer un avocat par userId auth-service")
    public ResponseEntity<AvocatResponse> getAvocatByUserId(@PathVariable Long userId) {
        log.info("GET /api/v1/avocats/by-user-id/{}", userId);
        return ResponseEntity.ok(avocatService.getAvocatByUserId(userId));
    }

   @GetMapping
   @Operation(summary = "Lister tous les avocats avec pagination")
   public ResponseEntity<PageResponse<AvocatSummaryResponse>> getAllAvocats(
     @RequestParam(required = false) List<String> specialites,
     @RequestParam(required = false) String ville,
     @RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "10") int size) {
     return ResponseEntity.ok(avocatService.getAllAvocats(specialites, ville, page, size));
}

    @GetMapping("/statut/{statut}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Filtrer les avocats par statut — usage admin")
    public ResponseEntity<PageResponse<AvocatSummaryResponse>> getAvocatsByStatut(
            @PathVariable StatutAvocatEnum statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/v1/avocats/statut/{}?page={}&size={}", statut, page, size);
        return ResponseEntity.ok(avocatService.getAvocatsByStatut(statut, page, size));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour le profil avocat")
    public ResponseEntity<AvocatResponse> updateAvocat(
            @PathVariable Long id,
            @Valid @RequestBody AvocatUpdateRequest request) {
        log.info("PUT /api/v1/avocats/{}", id);
        return ResponseEntity.ok(avocatService.updateAvocat(id, request));
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Changer le statut d'un avocat — usage admin")
    public ResponseEntity<AvocatResponse> updateStatut(
            @PathVariable Long id,
            @RequestParam StatutAvocatEnum statut) {
        log.info("PATCH /api/v1/avocats/{}/statut?statut={}", id, statut);
        return ResponseEntity.ok(avocatService.updateStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un profil avocat")
    public ResponseEntity<Void> deleteAvocat(@PathVariable Long id) {
        log.info("DELETE /api/v1/avocats/{}", id);
        avocatService.deleteAvocat(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Uploader la photo de profil d'un avocat")
    public ResponseEntity<String> uploadPhoto(
          @PathVariable Long id,
          @RequestParam("file") MultipartFile file) {
       log.info("POST /api/v1/avocats/{}/photo", id);
       String photoUrl = avocatService.uploadPhoto(id, file);
       return ResponseEntity.ok(photoUrl);
    }

    @GetMapping("/photos/{filename}")
    @Operation(summary = "Récupérer une photo de profil par nom de fichier")
    public ResponseEntity<Resource> getPhoto(@PathVariable String filename) throws IOException {
        log.info("GET /api/v1/avocats/photos/{}", filename);
        Path file = Paths.get(uploadDir).resolve(filename).normalize();

       if (!Files.exists(file) || !file.startsWith(Paths.get(uploadDir).normalize())) {
          return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(file.toUri());
        String contentType = Files.probeContentType(file);

        return ResponseEntity.ok()
             .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
             .body(resource);
    }

    @PostMapping(value = "/{id}/documents/{type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Uploader un document (carte pro, diplome, piece identite recto/verso)")
    public ResponseEntity<String> uploadDocument(
          @PathVariable Long id,
          @PathVariable DocumentTypeEnum type,
          @RequestParam(value = "typePiece", required = false) TypePieceIdentiteEnum typePiece,
          @RequestParam("file") MultipartFile file) {
       log.info("POST /api/v1/avocats/{}/documents/{}?typePiece={}", id, type, typePiece);
       String documentUrl = avocatService.uploadDocument(id, type, typePiece, file);
       return ResponseEntity.ok(documentUrl);
    }
  
    @GetMapping("/documents/{filename}")
    @Operation(summary = "Récupérer un document par nom de fichier")
    public ResponseEntity<Resource> getDocument(@PathVariable String filename) throws IOException {
        log.info("GET /api/v1/avocats/documents/{}", filename);
        Path file = Paths.get(uploadDocumentsDir).resolve(filename).normalize();

        if (!Files.exists(file) || !file.startsWith(Paths.get(uploadDocumentsDir).normalize())) {
          return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(file.toUri());
        String contentType = Files.probeContentType(file);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
            .body(resource);
    }

    @PostMapping("/recalculer-progressions")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Recalculer la progression de tous les avocats — usage admin ponctuel")
    public ResponseEntity<Void> recalculerToutesLesProgressions() {
        log.info("POST /api/v1/avocats/recalculer-progressions");
        avocatService.recalculerToutesLesProgressions();
        return ResponseEntity.ok().build();
    }
}