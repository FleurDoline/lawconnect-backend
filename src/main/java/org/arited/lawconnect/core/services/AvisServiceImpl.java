package org.arited.lawconnect.core.services;
import org.arited.lawconnect.core.entities.Avocat;
import org.arited.lawconnect.core.repositories.AvocatRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arited.lawconnect.core.entities.Avis;
import org.arited.lawconnect.core.entities.Consultation;
import org.arited.lawconnect.core.enums.StatutConsultationEnum;
import org.arited.lawconnect.core.repositories.AvisRepository;
import org.arited.lawconnect.core.repositories.ConsultationRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.arited.lawconnect.core.dtos.Request.AvisCreateRequest;
import org.arited.lawconnect.core.dtos.Response.AvisResponse;
import org.arited.lawconnect.core.dtos.Response.NoteMoyenneResponse;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;  
@Slf4j
@Service
@RequiredArgsConstructor
public class AvisServiceImpl implements AvisService {

    private final AvisRepository avisRepository;
    private final ConsultationRepository consultationRepository;
    private final AvocatRepository avocatRepository;

    @Override
    @Transactional
    public AvisResponse creerAvis(Long clientId, AvisCreateRequest request) {
        Consultation consultation = consultationRepository.findById(request.getConsultationId())
            .orElseThrow(() -> new EntityNotFoundException(
                "Consultation introuvable : " + request.getConsultationId()));

        if (!consultation.getClient().getUserId().equals(clientId)) {
            throw new AccessDeniedException("Cette consultation ne vous appartient pas");
        }

        if (consultation.getStatut() != StatutConsultationEnum.TERMINEE) {
            throw new IllegalStateException(
                "Vous ne pouvez laisser un avis qu'après une consultation terminée");
        }

        if (avisRepository.existsByConsultation_Id(consultation.getId())) {
            throw new IllegalStateException("Un avis a déjà été laissé pour cette consultation");
        }

        Avis avis = Avis.builder()
            .consultation(consultation)
            .note(request.getNote())
            .commentaire(request.getCommentaire())
            .build();

        Avis saved = avisRepository.save(avis);

        recalculerNoteMoyenne(consultation.getAvocat().getUserId());

        log.info("Avis id={} créé pour consultation={} par clientId={}",
            saved.getId(), consultation.getId(), clientId);

        return toResponse(saved);
    }

    private void recalculerNoteMoyenne(Long avocatUserId) {
        Double moyenne = avisRepository.findNoteMoyenneByAvocatId(avocatUserId);

        Avocat avocat = avocatRepository.findById(avocatUserId)
            .orElseThrow(() -> new EntityNotFoundException("Avocat introuvable : " + avocatUserId));

        avocat.setNoteMoyenne(moyenne != null
            ? BigDecimal.valueOf(moyenne).setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO);

        avocatRepository.save(avocat);
    }

    @Override
    public List<AvisResponse> getAvisPourAvocat(Long avocatUserId) {
        return avisRepository.findByConsultation_Avocat_UserIdOrderByCreatedAtDesc(avocatUserId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public NoteMoyenneResponse getNoteMoyenne(Long avocatUserId) {
        Double moyenne = avisRepository.findNoteMoyenneByAvocatId(avocatUserId);
        Long total = avisRepository.findNombreTotalAvisByAvocatId(avocatUserId);
        return NoteMoyenneResponse.builder()
            .moyenne(moyenne)
            .nombreAvis(total)
            .build();
    }

    private AvisResponse toResponse(Avis avis) {
        return AvisResponse.builder()
            .id(avis.getId())
            .consultationId(avis.getConsultation().getId())
            .note(avis.getNote())
            .commentaire(avis.getCommentaire())
            .nomClient(avis.getConsultation().getNomComplet())
            .createdAt(avis.getCreatedAt())
            .build();
    }
}