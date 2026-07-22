package org.arited.lawconnect.core.services;

import org.arited.lawconnect.core.dtos.Request.ConsultationCreateRequest;
import org.arited.lawconnect.core.dtos.Response.ConsultationAvocatSummaryResponse;
import org.arited.lawconnect.core.dtos.Response.ConsultationDetailResponse;
import org.arited.lawconnect.core.dtos.Response.ConsultationResponse;
import org.arited.lawconnect.core.dtos.Response.ConsultationSummaryResponse;

import java.util.List;

public interface ConsultationService {
    ConsultationResponse createConsultation(Long clientId, ConsultationCreateRequest request);
    List<ConsultationSummaryResponse> getConsultationsForClient(Long clientId);
    ConsultationDetailResponse getConsultationDetail(Long clientId, Long consultationId);

    /** Demandes de consultation reçues par l'avocat connecté (userId de l'avocat) */
    List<ConsultationAvocatSummaryResponse> getConsultationsForAvocat(Long avocatUserId);

    /** Accepter une demande de consultation et fixer la date/heure/mode du rendez-vous */
    ConsultationResponse accepterConsultation(Long avocatUserId, Long consultationId);
}