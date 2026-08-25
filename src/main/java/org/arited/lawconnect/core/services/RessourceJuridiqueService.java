package org.arited.lawconnect.core.services;

import org.arited.lawconnect.core.dtos.Pagination.PageResponse;
import org.arited.lawconnect.core.dtos.Request.RessourceJuridiqueCreateRequest;
import org.arited.lawconnect.core.dtos.Response.RessourceJuridiqueResponse;
import org.springframework.web.multipart.MultipartFile;

public interface RessourceJuridiqueService {

    PageResponse<RessourceJuridiqueResponse> getAll(Long specialiteId, int page, int size);

    RessourceJuridiqueResponse create(RessourceJuridiqueCreateRequest request, MultipartFile fichier);

    void delete(Long id);
}