package org.arited.lawconnect.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;

import org.arited.lawconnect.core.dtos.Pagination.PageResponse;
import org.arited.lawconnect.core.dtos.Request.AvocatCreateRequest;
import org.arited.lawconnect.core.dtos.Request.AvocatUpdateRequest;
import org.arited.lawconnect.core.dtos.Response.AvocatResponse;
import org.arited.lawconnect.core.dtos.Response.AvocatSummaryResponse;
import org.arited.lawconnect.core.entities.Avocat;
import org.arited.lawconnect.core.entities.SpecialiteDroit;
import org.arited.lawconnect.core.enums.StatutAvocatEnum;
import org.arited.lawconnect.core.exceptions.DuplicateResourceException;
import org.arited.lawconnect.core.exceptions.ResourceNotFoundException;
import org.arited.lawconnect.core.mappers.AvocatMapper;
import org.arited.lawconnect.core.repositories.AvocatRepository;
import org.arited.lawconnect.core.repositories.SpecialiteDroitRepository;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import java.util.Set;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AvocatServiceImpl implements AvocatService {

    private final AvocatRepository avocatRepository;
    private final AvocatMapper avocatMapper;
    private final PasswordEncoder passwordEncoder;
    private final SpecialiteDroitRepository specialiteDroitRepository; 
    @Value("${app.upload.dir:uploads/photos}")
    private String uploadDir;

    @Override
    @Transactional
    public AvocatResponse createAvocat(AvocatCreateRequest request) {
        log.info("Creating avocat profile for userId={}", request.getUserId());

        if (avocatRepository.existsByUserId(request.getUserId())) {
            throw new DuplicateResourceException(
                "Un profil avocat existe déjà pour userId=" + request.getUserId()
            );
        }
        if (avocatRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException(
                "Un compte avec l'email " + request.getEmail() + " existe déjà"
            );
        }

        Avocat avocat = avocatMapper.toEntity(request);
        avocat.setFullName(request.getPrenom() + " " + request.getNom());
        avocat.setPassword(passwordEncoder.encode(request.getPassword()));
        avocat.setStatut(StatutAvocatEnum.EN_ATTENTE);
        avocat.setProgression(calculateProgression(avocat));

        Avocat saved = avocatRepository.save(avocat);
        log.info("Avocat profile created with id={}", saved.getUserId());
        return avocatMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AvocatResponse getAvocatById(Long id) {
        log.info("Fetching avocat by id={}", id);
        return avocatMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public AvocatResponse getAvocatByUserId(Long userId) {
        log.info("Fetching avocat by userId={}", userId);
        Avocat avocat = avocatRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Avocat introuvable avec userId=" + userId
                ));
        return avocatMapper.toResponse(avocat);
    }

   @Override
   @Transactional(readOnly = true)
    public PageResponse<AvocatSummaryResponse> getAllAvocats(
        List<String> specialites, String ville, int page, int size) {

    log.info("Fetching avocats — specialites={}, ville={}, page={}, size={}", specialites, ville, page, size);
    Pageable pageable = PageRequest.of(page, size);

    List<String> normalizedSpecialites = (specialites != null && !specialites.isEmpty()) ? specialites : null;
    String normalizedVille = (ville != null && !ville.trim().isEmpty()) ? ville.trim() : null;

    Page<Avocat> avocatPage = avocatRepository.findBySpecialitesAndVille(
            normalizedSpecialites, normalizedVille, pageable);

    Page<AvocatSummaryResponse> result = avocatPage.map(avocatMapper::toSummaryResponse);
    return PageResponse.of(result);
}

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AvocatSummaryResponse> getAvocatsByStatut(StatutAvocatEnum statut, int page, int size) {
        log.info("Fetching avocats by statut={} — page={}, size={}", statut, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("creeLe").descending());
        Page<AvocatSummaryResponse> result = avocatRepository.findByStatut(statut, pageable)
                .map(avocatMapper::toSummaryResponse);
        return PageResponse.of(result);
    }

    @Override
    @Transactional
    public AvocatResponse updateAvocat(Long id, AvocatUpdateRequest request) {
        log.info("Updating avocat id={}", id);
        Avocat avocat = findOrThrow(id);

        avocatMapper.updateEntity(request, avocat);

        if (request.getSpecialiteIds() != null) {
            Set<SpecialiteDroit> specialites = new HashSet<>(
                specialiteDroitRepository.findAllById(request.getSpecialiteIds())
            );
            avocat.setSpecialites(specialites);
        }

        avocat.setProgression(calculateProgression(avocat));

        Avocat updated = avocatRepository.save(avocat);
        log.info("Avocat id={} updated — progression={}%", id, updated.getProgression());
        return avocatMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public AvocatResponse updateStatut(Long id, StatutAvocatEnum statut) {
        log.info("Updating statut of avocat id={} to {}", id, statut);
        Avocat avocat = findOrThrow(id);
        avocat.setStatut(statut);
        return avocatMapper.toResponse(avocatRepository.save(avocat));
    }

    @Override
    @Transactional
    public void deleteAvocat(Long id) {
        log.info("Deleting avocat id={}", id);
        if (!avocatRepository.existsById(id)) {
            throw new ResourceNotFoundException("Avocat introuvable avec id=" + id);
        }
        avocatRepository.deleteById(id);
        log.info("Avocat id={} deleted", id);
    }

    // --- Private helpers ---

    private Avocat findOrThrow(Long id) {
        return avocatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Avocat introuvable avec id=" + id
                ));
    }
    // 16 criteres pour un profil complet, chacun valant 100/16 ≈ 6.25%
private int calculateProgression(Avocat avocat) {
    int totalCriteres = 16;
    int rempli = 0;

    if (isNotBlank(avocat.getPrenom()))              rempli++;
    if (isNotBlank(avocat.getNom()))                 rempli++;
    if (isNotBlank(avocat.getEmail()))                rempli++;
    if (isNotBlank(avocat.getPassword()))            rempli++;
    if (isNotBlank(avocat.getPhoto()))                rempli++;
    if (avocat.getSpecialites() != null && !avocat.getSpecialites().isEmpty()) rempli++;
    if (isNotBlank(avocat.getBio()))                  rempli++;
    if (isNotBlank(avocat.getAdresseCabinet()))       rempli++;
    if (isNotBlank(avocat.getVille()))                rempli++;
    if (avocat.getExperience() != null)                rempli++;
    if (isNotBlank(avocat.getDiplome()))              rempli++;
    if (isNotBlank(avocat.getCarteProfessionnel()))   rempli++;
    if (isNotBlank(avocat.getLienAgenda()))           rempli++;
    if (isNotBlank(avocat.getPieceIdentite()))        rempli++;
    if (isNotBlank(avocat.getTelephone()))            rempli++;
    if (avocat.getStatut() == StatutAvocatEnum.VALIDE) rempli++;

    return Math.round((rempli * 100f) / totalCriteres);
}

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    @Override
@Transactional
public String uploadPhoto(Long id, MultipartFile file) {
    log.info("Uploading photo for avocat id={}", id);
    Avocat avocat = findOrThrow(id);

    if (file.isEmpty() || file.getContentType() == null || !file.getContentType().startsWith("image/")) {
        throw new IllegalArgumentException("Le fichier doit être une image valide");
    }

    try {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + extension;
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Supprimer l'ancienne photo si elle existe
        if (isNotBlank(avocat.getPhoto())) {
            Path oldFile = uploadPath.resolve(Paths.get(avocat.getPhoto()).getFileName());
            Files.deleteIfExists(oldFile);
        }

        String photoUrl = "/api/v1/avocats/photos/" + filename;
        avocat.setPhoto(photoUrl);
        avocat.setProgression(calculateProgression(avocat));
        avocatRepository.save(avocat);

        log.info("Photo uploaded for avocat id={} -> {}", id, photoUrl);
        return photoUrl;

    } catch (IOException e) {
        throw new RuntimeException("Erreur lors de l'upload de la photo", e);
    }
}

private String getExtension(String filename) {
    if (filename == null || !filename.contains(".")) return "";
    return filename.substring(filename.lastIndexOf("."));
}

@Override
@Transactional
public void recalculerToutesLesProgressions() {
    log.info("Recalcul de la progression pour tous les avocats");
    List<Avocat> tousLesAvocats = avocatRepository.findAll();

    for (Avocat avocat : tousLesAvocats) {
        avocat.setProgression(calculateProgression(avocat));
    }

    avocatRepository.saveAll(tousLesAvocats);
    log.info("Progression recalculee pour {} avocats", tousLesAvocats.size());
}
}