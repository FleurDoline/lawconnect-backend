package org.arited.lawconnect.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;

import org.arited.lawconnect.core.dtos.Pagination.PageResponse;
import org.arited.lawconnect.core.dtos.Request.AvocatCreateRequest;
import org.arited.lawconnect.core.dtos.Request.AvocatUpdateRequest;
import org.arited.lawconnect.core.dtos.Response.AdminStatsResponse;
import org.arited.lawconnect.core.dtos.Response.AvocatResponse;
import org.arited.lawconnect.core.dtos.Response.AvocatSummaryResponse;
import org.arited.lawconnect.core.entities.Avocat;
import org.arited.lawconnect.core.entities.SpecialiteDroit;
import org.arited.lawconnect.core.enums.DocumentTypeEnum;
import org.arited.lawconnect.core.enums.StatutAvocatEnum;
import org.arited.lawconnect.core.enums.StatutPaiementEnum;
import org.arited.lawconnect.core.enums.TypePieceIdentiteEnum;
import org.arited.lawconnect.core.exceptions.DuplicateResourceException;
import org.arited.lawconnect.core.exceptions.ResourceNotFoundException;
import org.arited.lawconnect.core.mappers.AvocatMapper;
import org.arited.lawconnect.core.repositories.AbonnementRepository;
import org.arited.lawconnect.core.repositories.AvocatRepository;
import org.arited.lawconnect.core.repositories.DisponibiliteRepository;
import org.arited.lawconnect.core.repositories.SpecialiteDroitRepository;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
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
    private final DisponibiliteRepository disponibiliteRepository;
    private final AbonnementRepository abonnementRepository;
    @Value("${app.upload.dir:uploads/photos}")
    private String uploadDir;
    @Value("${app.upload.documents.dir:uploads/documents}")
    private String uploadDocumentsDir;

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
    Avocat avocat = findOrThrow(id);
    AvocatResponse response = avocatMapper.toResponse(avocat);
    response.setGereDisponibilites(disponibiliteRepository.existsByAvocat_UserId(avocat.getUserId()));
    return response;
}

@Override
@Transactional(readOnly = true)
public AvocatResponse getAvocatByUserId(Long userId) {
    log.info("Fetching avocat by userId={}", userId);
    Avocat avocat = avocatRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Avocat introuvable avec userId=" + userId
            ));
    AvocatResponse response = avocatMapper.toResponse(avocat);
    response.setGereDisponibilites(disponibiliteRepository.existsByAvocat_UserId(avocat.getUserId()));
    return response;
}

   @Override
   @Transactional(readOnly = true)
   public PageResponse<AvocatSummaryResponse> getAllAvocats(
      List<String> specialites, String ville, int page, int size) {

      log.info("Fetching avocats — specialites={}, ville={}, page={}, size={}", specialites, ville, page, size);
       Pageable pageable = PageRequest.of(page, size);

       List<String> normalizedSpecialites = (specialites != null && !specialites.isEmpty())
        ? specialites.stream().map(String::toLowerCase).toList()
          : null;
       String normalizedVille = (ville != null && !ville.trim().isEmpty()) ? ville.trim() : null;

       Page<Avocat> avocatPage = avocatRepository.findBySpecialitesAndVille(
            normalizedSpecialites, normalizedVille, pageable);

       Page<AvocatSummaryResponse> result = avocatPage.map(avocat -> {
          AvocatSummaryResponse resp = avocatMapper.toSummaryResponse(avocat);
          resp.setGereDisponibilites(disponibiliteRepository.existsByAvocat_UserId(avocat.getUserId()));
          return resp;
     });

      return PageResponse.of(result);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AvocatSummaryResponse> getAvocatsByStatut(StatutAvocatEnum statut, int page, int size) {
       log.info("Fetching avocats by statut={} — page={}, size={}", statut, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AvocatSummaryResponse> result = avocatRepository.findByStatut(statut, pageable)
            .map(avocat -> {
                AvocatSummaryResponse resp = avocatMapper.toSummaryResponse(avocat);
                resp.setGereDisponibilites(disponibiliteRepository.existsByAvocat_UserId(avocat.getUserId()));
                return resp;
            });
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

   if (statut == StatutAvocatEnum.VALIDE && !hasAuMoinsUnDocument(avocat)) {
       throw new IllegalStateException(
           "Impossible de valider un avocat sans document fourni (diplôme, carte professionnelle ou pièce d'identité)"
       );
   }

   avocat.setStatut(statut);

   if (statut == StatutAvocatEnum.VALIDE || statut == StatutAvocatEnum.REJETE) {
    Long adminId = getCurrentUserId();
    avocat.setValidBy(adminId);
    avocat.setValidAt(java.time.LocalDate.now());
  }

  return avocatMapper.toResponse(avocatRepository.save(avocat));
}

private boolean hasAuMoinsUnDocument(Avocat avocat) {
    return isNotBlank(avocat.getDiplome())
        || isNotBlank(avocat.getCarteProfessionnel())
        || isNotBlank(avocat.getPieceIdentiteRecto());
}

    private Long getCurrentUserId() {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getPrincipal() == null) {
        return null;
    }

    Object principal = auth.getPrincipal();
    if (principal instanceof org.arited.lawconnect.security.models.UserPrincipal userPrincipal) {
        return userPrincipal.getId();
    }

    return null;
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

    @Override
    @Transactional
    public void recalculerProgression(Long avocatId) {
        log.info("Recalcul de la progression pour avocat id={}", avocatId);
        Avocat avocat = findOrThrow(avocatId);
        avocat.setProgression(calculateProgression(avocat));
        avocatRepository.save(avocat);
    }

    // --- Private helpers ---

    private Avocat findOrThrow(Long id) {
        return avocatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Avocat introuvable avec id=" + id
                ));
    }
    // 17 criteres pour un profil complet, chacun valant 100/17 ≈ 5.88%
private int calculateProgression(Avocat avocat) {
    int totalCriteres = 17;
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
    if (isNotBlank(avocat.getPieceIdentiteRecto()))   rempli++;
    if (isNotBlank(avocat.getTelephone()))            rempli++;
    if (avocat.getStatut() == StatutAvocatEnum.VALIDE) rempli++;
    if (hasAbonnementActif(avocat.getUserId()))        rempli++;

    return Math.round((rempli * 100f) / totalCriteres);
}

    private boolean hasAbonnementActif(Long avocatUserId) {
        if (avocatUserId == null) return false;
        return abonnementRepository
                .findByAvocatUserIdAndStatut(avocatUserId, StatutPaiementEnum.PAYE)
                .isPresent();
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

@Override
@Transactional
public String uploadDocument(Long id, DocumentTypeEnum type, TypePieceIdentiteEnum typePieceIdentite, MultipartFile file) {
    log.info("Uploading document type={} for avocat id={}", type, id);
    Avocat avocat = findOrThrow(id);

    if (file.isEmpty()) {
        throw new IllegalArgumentException("Fichier vide");
    }

    String contentType = file.getContentType();
    boolean isImage = contentType != null && contentType.startsWith("image/");
    boolean isPdf = contentType != null && contentType.equals("application/pdf");
    if (!isImage && !isPdf) {
        throw new IllegalArgumentException("Le fichier doit être une image ou un PDF");
    }

    if (type == DocumentTypeEnum.PIECE_IDENTITE_RECTO && typePieceIdentite == null) {
        throw new IllegalArgumentException(
            "Le type de pièce d'identité (CNI, PASSEPORT, PERMIS_CONDUIRE) est requis pour le recto"
        );
    }

    try {
        Path uploadPath = Paths.get(uploadDocumentsDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + extension;
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String documentUrl = "/api/v1/avocats/documents/" + filename;

        switch (type) {
            case CARTE_PROFESSIONNELLE -> {
                deleteOldDocument(uploadPath, avocat.getCarteProfessionnel());
                avocat.setCarteProfessionnel(documentUrl);
            }
            case DIPLOME -> {
                deleteOldDocument(uploadPath, avocat.getDiplome());
                avocat.setDiplome(documentUrl);
            }
            case PIECE_IDENTITE_RECTO -> {
                deleteOldDocument(uploadPath, avocat.getPieceIdentiteRecto());
                avocat.setPieceIdentiteRecto(documentUrl);
                avocat.setTypePieceIdentite(typePieceIdentite);
            }
            case PIECE_IDENTITE_VERSO -> {
                if (avocat.getTypePieceIdentite() != TypePieceIdentiteEnum.CNI) {
                    throw new IllegalStateException(
                        "Le verso n'est requis que pour une CNI. Uploadez d'abord le recto avec typePiece=CNI."
                    );
                }
                deleteOldDocument(uploadPath, avocat.getPieceIdentiteVerso());
                avocat.setPieceIdentiteVerso(documentUrl);
            }
        }

        avocat.setProgression(calculateProgression(avocat));
        avocatRepository.save(avocat);

        log.info("Document {} uploaded for avocat id={} -> {}", type, id, documentUrl);
        return documentUrl;

    } catch (IOException e) {
        throw new RuntimeException("Erreur lors de l'upload du document", e);
    }
}

private void deleteOldDocument(Path uploadPath, String existingUrl) {
    if (isNotBlank(existingUrl)) {
        try {
            Path oldFile = uploadPath.resolve(Paths.get(existingUrl).getFileName());
            Files.deleteIfExists(oldFile);
        } catch (Exception ignored) {}
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

public AdminStatsResponse getStats() {
    long total = avocatRepository.count();
    long valides = avocatRepository.countByStatut(StatutAvocatEnum.VALIDE);
    long enAttente = avocatRepository.countByStatut(StatutAvocatEnum.EN_ATTENTE);
    double taux = total > 0 ? Math.round((valides * 1000.0 / total)) / 10.0 : 0.0;
    long abonnementsActifs = abonnementRepository.countByStatut(StatutPaiementEnum.PAYE);

    return AdminStatsResponse.builder()
        .totalAvocats(total)
        .avocatsValides(valides)
        .avocatsEnAttente(enAttente)
        .tauxConversion(taux)
        .abonnementsActifs(abonnementsActifs)
        .build();
}
}