package org.arited.lawconnect.core.controllers;

import lombok.RequiredArgsConstructor;
import org.arited.lawconnect.core.dtos.Pagination.PageResponse;
import org.arited.lawconnect.core.dtos.Request.RessourceJuridiqueCreateRequest;
import org.arited.lawconnect.core.dtos.Response.RessourceJuridiqueResponse;
import org.arited.lawconnect.core.services.RessourceJuridiqueService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/ressources")
@RequiredArgsConstructor
public class RessourceJuridiqueController {

    private final RessourceJuridiqueService ressourceJuridiqueService;

    @Value("${app.upload.ressources.dir:uploads/ressources}")
    private String uploadDir;

    @GetMapping
    public ResponseEntity<PageResponse<RessourceJuridiqueResponse>> getAll(
            @RequestParam(required = false) Long specialiteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ressourceJuridiqueService.getAll(specialiteId, page, size));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RessourceJuridiqueResponse> create(
            @RequestParam String titre,
            @RequestParam String auteur,
            @RequestParam(required = false) String description,
            @RequestParam Long specialiteId,
            @RequestParam MultipartFile fichier) {

        RessourceJuridiqueCreateRequest request = new RessourceJuridiqueCreateRequest(
                titre, auteur, description, specialiteId);
        RessourceJuridiqueResponse response = ressourceJuridiqueService.create(request, fichier);
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ressourceJuridiqueService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fichiers/{filename}")
    public ResponseEntity<Resource> getFichier(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
