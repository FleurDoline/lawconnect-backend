package org.arited.lawconnect.core.services;

import org.arited.lawconnect.core.dtos.Request.ConsultationCreateRequest;
import org.arited.lawconnect.core.dtos.Response.ConsultationAvocatSummaryResponse;
import org.arited.lawconnect.core.dtos.Response.ConsultationDetailResponse;
import org.arited.lawconnect.core.dtos.Response.ConsultationResponse;
import org.arited.lawconnect.core.dtos.Response.ConsultationSummaryResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ConsultationService {
    ConsultationResponse createConsultation(Long clientId, ConsultationCreateRequest request);
    List<ConsultationSummaryResponse> getConsultationsForClient(Long clientId);
    ConsultationDetailResponse getConsultationDetail(Long clientId, Long consultationId);

    /** Demandes de consultation reçues par l'avocat connecté (userId de l'avocat) */
    List<ConsultationAvocatSummaryResponse> getConsultationsForAvocat(Long avocatUserId);

    /** Prochain(s) rendez-vous confirmé(s) et à venir pour l'avocat connecté, triés par date croissante */
    List<ConsultationAvocatSummaryResponse> getProchainsRendezVous(Long avocatUserId);

    /** Accepter une demande de consultation et fixer la date/heure/mode du rendez-vous */
    ConsultationResponse accepterConsultation(Long avocatUserId, Long consultationId);

    List<LocalTime> getCreneauxDisponibles(Long avocatId, LocalDate date);

    /** Le client annule/refuse sa propre demande, uniquement si elle est encore EN_ATTENTE */
    ConsultationResponse refuserConsultation(Long clientId, Long consultationId);
}