package org.arited.lawconnect.core.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arited.lawconnect.core.dtos.Request.ConsultationCreateRequest;
import org.arited.lawconnect.core.dtos.Response.ConsultationAvocatSummaryResponse;
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
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;
import org.arited.lawconnect.core.entities.Disponibilite;
import org.arited.lawconnect.core.repositories.DisponibiliteRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final AvocatRepository avocatRepository;
    private final ClientRepository clientRepository;
    private final ConsultationMapper consultationMapper;
    private final EmailService emailService;
    private final DisponibiliteRepository disponibiliteRepository;

    @Override
    @Transactional
    public ConsultationResponse createConsultation(Long clientId, ConsultationCreateRequest request) {
      Client client = clientRepository.findById(clientId)
        .orElseThrow(() -> new EntityNotFoundException("Client introuvable : " + clientId));

      Avocat avocat = avocatRepository.findById(request.getAvocatId())
        .orElseThrow(() -> new EntityNotFoundException("Avocat introuvable : " + request.getAvocatId()));

      boolean gereDisponibilites = disponibiliteRepository.existsByAvocat_UserId(avocat.getUserId());

      if (gereDisponibilites) {
          // L'avocat gère ses créneaux sur la plateforme : une date/heure valide est obligatoire
          if (request.getDateRendezVous() == null) {
              throw new IllegalStateException(
                  "Veuillez choisir un créneau parmi les disponibilités de cet avocat."
              );
          }

          LocalDate dateDemandee = request.getDateRendezVous().toLocalDate();
          LocalTime heureDemandee = request.getDateRendezVous().toLocalTime();

          List<LocalTime> creneauxLibres = getCreneauxDisponibles(avocat.getUserId(), dateDemandee);

          if (!creneauxLibres.contains(heureDemandee)) {
              throw new IllegalStateException(
                  "Ce créneau n'est plus disponible. Merci d'en choisir un autre."
              );
          }
      } else {
          // L'avocat gère son agenda en externe : il doit avoir configuré son lien
          if (avocat.getLienAgenda() == null || avocat.getLienAgenda().isBlank()) {
              throw new IllegalStateException(
                  "Cet avocat n'a pas encore configuré son agenda. Merci de réessayer plus tard."
              );
          }
      }

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
      consultation.setDateRendezVous(gereDisponibilites ? request.getDateRendezVous() : null);
      consultation.setModeConsultation(request.getModeConsultation());
      consultation.setStatut(StatutConsultationEnum.EN_ATTENTE);

      Consultation saved = consultationRepository.save(consultation);

      log.info("Consultation créée id={} client={} avocat={} dateRendezVous={}",
            saved.getId(), clientId, avocat.getUserId(), saved.getDateRendezVous());

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

    @Override
    public List<ConsultationAvocatSummaryResponse> getConsultationsForAvocat(Long avocatUserId) {
        return consultationRepository.findByAvocat_UserIdOrderByCreatedAtDesc(avocatUserId)
            .stream()
            .map(consultationMapper::toAvocatSummary)
            .collect(Collectors.toList());
    }

    @Override
    public List<ConsultationAvocatSummaryResponse> getProchainsRendezVous(Long avocatUserId) {
    return consultationRepository
        .findProchainsRendezVous(avocatUserId, StatutConsultationEnum.CONFIRMEE, LocalDateTime.now())
        .stream()
        .map(consultationMapper::toAvocatSummary)
        .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConsultationResponse accepterConsultation(Long avocatUserId, Long consultationId) {
      Consultation consultation = consultationRepository.findById(consultationId)
          .orElseThrow(() -> new EntityNotFoundException("Consultation introuvable : " + consultationId));

      Avocat avocat = consultation.getAvocat();

      if (!avocat.getUserId().equals(avocatUserId)) {
         throw new AccessDeniedException("Cette consultation ne vous appartient pas");
       }

       if (consultation.getStatut() != StatutConsultationEnum.EN_ATTENTE) {
          throw new IllegalStateException("Cette demande a déjà été traitée");
        }

       consultation.setStatut(StatutConsultationEnum.CONFIRMEE);
       Consultation saved = consultationRepository.save(consultation);

        log.info("Consultation id={} acceptée par avocatUserId={}", saved.getId(), avocatUserId);

        boolean utiliseCreneauNatif = saved.getDateRendezVous() != null;

       if (utiliseCreneauNatif) {
        emailService.sendConfirmationCreneauNatif(
          saved.getEmail(),
          saved.getNomComplet(),
          avocat.getFullName(),
          saved.getDateRendezVous(),
          saved.getModeConsultation()
        );
      } else {
          if (avocat.getLienAgenda() == null || avocat.getLienAgenda().isBlank()) {
             throw new IllegalStateException(
              "Veuillez configurer votre lien d'agenda dans Paramètres avant d'accepter une demande."
            );
          }

          emailService.sendLienAgenda(
            saved.getEmail(),
            saved.getNomComplet(),
            avocat.getFullName(),
            avocat.getLienAgenda()
          );
       }

       return ConsultationResponse.builder()
         .id(saved.getId())
         .statut(saved.getStatut().name())
         .message("Rendez-vous accepté, un email a été envoyé au client")
         .build();
    }

    @Override
    @Transactional
    public ConsultationResponse refuserConsultation(Long clientId, Long consultationId) {
    Consultation consultation = consultationRepository.findById(consultationId)
        .orElseThrow(() -> new EntityNotFoundException("Consultation introuvable : " + consultationId));

       if (!consultation.getClient().getUserId().equals(clientId)) {
         throw new AccessDeniedException("Cette consultation ne vous appartient pas");
       }

       if (consultation.getStatut() != StatutConsultationEnum.EN_ATTENTE) {
          throw new IllegalStateException(
            "Cette demande ne peut plus être annulée, elle a déjà été traitée."
          );
        }

        consultation.setStatut(StatutConsultationEnum.ANNULEE);
        Consultation saved = consultationRepository.save(consultation);

        log.info("Consultation id={} annulée par clientId={}", saved.getId(), clientId);

        return ConsultationResponse.builder()
        .id(saved.getId())
        .statut(saved.getStatut().name())
        .message("Votre demande de consultation a été annulée")
        .build();
    }

    @Override
    public List<LocalTime> getCreneauxDisponibles(Long avocatId, LocalDate date) {
    DayOfWeek jour = date.getDayOfWeek();

    Disponibilite dispo = disponibiliteRepository
            .findByAvocatIdAndJour(avocatId, jour)
            .orElse(null);

    if (dispo == null) {
        return List.of();
    }

    List<LocalTime> creneauxGeneres = new ArrayList<>();
    LocalTime heureCourante = dispo.getHeureDebut();

    while (!heureCourante.plusHours(1).isAfter(dispo.getHeureFin())) {
        if (!heureCourante.equals(LocalTime.of(12, 0))) {
            creneauxGeneres.add(heureCourante);
        }
        heureCourante = heureCourante.plusHours(1);
    }

    LocalDateTime debutJournee = date.atStartOfDay();
    LocalDateTime finJournee = date.atTime(23, 59, 59);

    List<Consultation> consultationsExistantes = consultationRepository
            .findByAvocatIdAndDateRendezVousBetweenAndStatutIn(
                    avocatId, debutJournee, finJournee,
                    List.of(StatutConsultationEnum.EN_ATTENTE, StatutConsultationEnum.CONFIRMEE)
            );

    Set<LocalTime> heuresPrises = consultationsExistantes.stream()
            .map(c -> c.getDateRendezVous().toLocalTime())
            .collect(Collectors.toSet());

    return creneauxGeneres.stream()
            .filter(c -> !heuresPrises.contains(c))
            .toList();
    }   
}