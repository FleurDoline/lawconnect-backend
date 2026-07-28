package org.arited.lawconnect.core.services;

import org.arited.lawconnect.core.dtos.DisponibiliteDTO;
import org.arited.lawconnect.core.dtos.Request.DisponibiliteCreateRequest;

import java.util.List;

public interface DisponibiliteService {
    List<DisponibiliteDTO> getDisponibilites(Long avocatUserId);
    List<DisponibiliteDTO> remplacerDisponibilites(Long avocatUserId, List<DisponibiliteCreateRequest> requests);
    void supprimerDisponibilite(Long avocatUserId, Long disponibiliteId);
}
