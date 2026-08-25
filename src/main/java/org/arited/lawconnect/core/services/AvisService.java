package org.arited.lawconnect.core.services;

import org.arited.lawconnect.core.dtos.Request.AvisCreateRequest;
import org.arited.lawconnect.core.dtos.Response.AvisResponse;
import org.arited.lawconnect.core.dtos.Response.NoteMoyenneResponse;

import java.util.List;

public interface AvisService {

    AvisResponse creerAvis(Long clientId, AvisCreateRequest request);

    List<AvisResponse> getAvisPourAvocat(Long avocatUserId);

    NoteMoyenneResponse getNoteMoyenne(Long avocatUserId);
}
