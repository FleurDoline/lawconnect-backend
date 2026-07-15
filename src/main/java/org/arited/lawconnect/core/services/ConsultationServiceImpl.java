package org.arited.lawconnect.core.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arited.lawconnect.core.dtos.Request.ConsultationCreateRequest;
import org.arited.lawconnect.core.dtos.Response.ConsultationDetailResponse;
import org.arited.lawconnect.core.dtos.Response.ConsultationResponse;
import org.arited.lawconnect.core.dtos.Response.ConsultationSummaryResponse;
import org.arited.lawconnect.core.entities.Avocat;
import org.arited.lawconnect.core.entities.Client;
import org.arited.lawconnect.core.entities.Consultation;
import org.arited.lawconnect.core.enums.StatutConsultationEnum;
import org.arited.lawconnect.core.mappers.ConsultationMapper;
import org.arited.lawconnect.core.repositories.AvocatRepository;
import org.arited.lawconnect.core.repositories.ClientRepository;
import org.arited.lawconnect.core.repositories.ConsultationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.arited.lawconnect.core.dtos.Response.ConsultationDetailResponse;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final AvocatRepository avocatRepository;
    private final ClientRepository clientRepository;
    private final ConsultationMapper consultationMapper;

    @Override
    @Transactional
    public ConsultationResponse createConsultation(Long clientId, ConsultationCreateRequest request) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + clientId));

        Avocat avocat = avocatRepository.findById(request.getAvocatId())
            .orElseThrow(() -> new EntityNotFoundException("Avocat introuvable : " + request.getAvocatId()));

        Consultation consultation = new Consultation();
        consultation.setClient(client);
        consultation.setAvocat(avocat);
        consultation.setFlowType(request.getFlowType());
        consultation.setEligibilite(request.getEligibilite());
        consultation.setTypePersonne(request.getTypePersonne());
        consultation.setMission(request.getMission());
        consultation.setAttentes(request.getAttentes());
        consultation.setUrgent(request.getUrgent());
        consultation.setSituation(request.getSituation());
        consultation.setNomComplet(request.getNomComplet());
        consultation.setTelephone(request.getTelephone());
        consultation.setEmail(request.getEmail());
        consultation.setVille(request.getVille());
        consultation.setContactPreference(request.getContactPreference());
        consultation.setStatut(StatutConsultationEnum.EN_ATTENTE);

        Consultation saved = consultationRepository.save(consultation);

        log.info("Consultation créée id={} client={} avocat={}", saved.getId(), clientId, avocat.getUserId());

        return ConsultationResponse.builder()
            .id(saved.getId())
            .statut(saved.getStatut().name())
            .message("Votre demande a bien été transmise à Maître " + avocat.getFullName())
            .build();
    }

    @Override
    public List<ConsultationSummaryResponse> getConsultationsForClient(Long clientId) {
        return consultationRepository.findByClient_UserIdOrderByCreatedAtDesc(clientId)
            .stream()
            .map(consultationMapper::toSummary)
            .collect(Collectors.toList());
    }

    @Override
    public ConsultationDetailResponse getConsultationDetail(Long clientId, Long consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new EntityNotFoundException("Consultation introuvable : " + consultationId));

        if (!consultation.getClient().getUserId().equals(clientId)) {
            throw new AccessDeniedException("Cette consultation ne vous appartient pas");
        }

        return consultationMapper.toDetail(consultation);
    }
}