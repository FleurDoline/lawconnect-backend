package org.arited.lawconnect.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arited.lawconnect.core.dtos.Pagination.PageResponse;
import org.arited.lawconnect.core.dtos.Request.RessourceJuridiqueCreateRequest;
import org.arited.lawconnect.core.dtos.Response.RessourceJuridiqueResponse;
import org.arited.lawconnect.core.entities.RessourceJuridique;
import org.arited.lawconnect.core.entities.SpecialiteDroit;
import org.arited.lawconnect.core.exceptions.ResourceNotFoundException;
import org.arited.lawconnect.core.mappers.RessourceJuridiqueMapper;
import org.arited.lawconnect.core.repositories.RessourceJuridiqueRepository;
import org.arited.lawconnect.core.repositories.SpecialiteDroitRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RessourceJuridiqueServiceImpl implements RessourceJuridiqueService {

    private final RessourceJuridiqueRepository ressourceJuridiqueRepository;
    private final SpecialiteDroitRepository specialiteDroitRepository;
    private final RessourceJuridiqueMapper ressourceJuridiqueMapper;

    @Value("${app.upload.ressources.dir:uploads/ressources}")
    private String uploadDir;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RessourceJuridiqueResponse> getAll(Long specialiteId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RessourceJuridiqueResponse> result = ressourceJuridiqueRepository
                .findAllWithFilter(specialiteId, pageable)
                .map(ressourceJuridiqueMapper::toResponse);
        return PageResponse.of(result);
    }

    @Override
    @Transactional
    public RessourceJuridiqueResponse create(RessourceJuridiqueCreateRequest request, MultipartFile fichier) {
        log.info("Creating ressource juridique: {}", request.getTitre());

        if (fichier.isEmpty() || fichier.getContentType() == null
                || !fichier.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("Le fichier doit être un PDF valide");
        }

        SpecialiteDroit specialite = specialiteDroitRepository.findById(request.getSpecialiteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Spécialité introuvable avec id=" + request.getSpecialiteId()));

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = UUID.randomUUID() + ".pdf";
            Path filePath = uploadPath.resolve(filename);
            Files.copy(fichier.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fichierUrl = "/api/v1/ressources/fichiers/" + filename;

            RessourceJuridique ressource = RessourceJuridique.builder()
                    .titre(request.getTitre())
                    .auteur(request.getAuteur())
                    .description(request.getDescription())
                    .specialite(specialite)
                    .cheminFichier(fichierUrl)
                    .build();

            RessourceJuridique saved = ressourceJuridiqueRepository.save(ressource);
            log.info("Ressource juridique id={} created -> {}", saved.getId(), fichierUrl);
            return ressourceJuridiqueMapper.toResponse(saved);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload du fichier", e);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!ressourceJuridiqueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ressource introuvable avec id=" + id);
        }
        ressourceJuridiqueRepository.deleteById(id);
    }
}
