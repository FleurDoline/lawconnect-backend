package org.arited.lawconnect.core.services;
import org.arited.lawconnect.core.dtos.Pagination.PageResponse;
import org.arited.lawconnect.core.dtos.Request.AvocatCreateRequest;
import org.arited.lawconnect.core.dtos.Request.AvocatUpdateRequest;
import org.arited.lawconnect.core.dtos.Response.AdminStatsResponse;
import org.arited.lawconnect.core.dtos.Response.AvocatResponse;
import org.arited.lawconnect.core.dtos.Response.AvocatSummaryResponse;
import org.arited.lawconnect.core.enums.DocumentTypeEnum;
import org.arited.lawconnect.core.enums.StatutAvocatEnum;
import org.arited.lawconnect.core.enums.TypePieceIdentiteEnum;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

public interface AvocatService {
AvocatResponse createAvocat(AvocatCreateRequest request);
AvocatResponse getAvocatById(Long id);
AvocatResponse getAvocatByUserId(Long userId);
PageResponse<AvocatSummaryResponse> getAllAvocats(List<String> specialites, String ville, int page, int size);
PageResponse<AvocatSummaryResponse> getAvocatsByStatut(StatutAvocatEnum statut, int page, int size);
AvocatResponse updateAvocat(Long id, AvocatUpdateRequest request);
AvocatResponse updateStatut(Long id, StatutAvocatEnum statut);
void deleteAvocat(Long id);
String uploadPhoto(Long id, MultipartFile file);
void recalculerToutesLesProgressions();
String uploadDocument(Long id, DocumentTypeEnum type, TypePieceIdentiteEnum typePieceIdentite, MultipartFile file);
void recalculerProgression(Long avocatId);
AdminStatsResponse getStats();
}