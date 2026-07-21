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

    // 10 profile fields, each worth 10% = 100% max
    private int calculateProgression(Avocat avocat) {
        int score = 0;
        if (avocat.getSpecialites() != null && !avocat.getSpecialites().isEmpty()) score += 10;
        if (isNotBlank(avocat.getBio()))              score += 10;
        if (isNotBlank(avocat.getPhoto()))            score += 10;
        if (isNotBlank(avocat.getAdresseCabinet()))   score += 10;
        if (isNotBlank(avocat.getVille()))            score += 10;
        if (avocat.getTarif() != null)                score += 10;
        if (avocat.getExperience() != null)           score += 10;
        if (isNotBlank(avocat.getDiplome()))          score += 10;
        if (isNotBlank(avocat.getCarteProfessionnel())) score += 10;
        return score;
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}