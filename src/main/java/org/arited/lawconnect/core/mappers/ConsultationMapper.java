package org.arited.lawconnect.core.mappers;

import org.arited.lawconnect.core.dtos.Response.ConsultationAvocatSummaryResponse;
import org.arited.lawconnect.core.dtos.Response.ConsultationDetailResponse;
import org.arited.lawconnect.core.dtos.Response.ConsultationSummaryResponse;
import org.arited.lawconnect.core.entities.Avocat;
import org.arited.lawconnect.core.entities.Consultation;
import org.arited.lawconnect.core.entities.SpecialiteDroit;
import org.arited.lawconnect.core.enums.StatutConsultationEnum;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class ConsultationMapper {

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("d MMMM", Locale.FRENCH);

    private static final DateTimeFormatter HEURE_FORMAT =
        DateTimeFormatter.ofPattern("HH:mm");

    public ConsultationSummaryResponse toSummary(Consultation consultation) {
        Avocat avocat = consultation.getAvocat();
        LocalDateTime affichee = dateAffichee(consultation);

        return ConsultationSummaryResponse.builder()
         .id(consultation.getId())
         .avocatNom(avocat.getFullName())
         .avocatInitiales(initiales(avocat.getFullName()))
         .avocatTelephone(avocat.getTelephone())
         .specialite(specialiteOf(avocat))
         .date(affichee.format(DATE_FORMAT))
         .heure(affichee.format(HEURE_FORMAT))
         .statut(consultation.getStatut().name())
         .mode(consultation.getModeConsultation())
         .avocatTelephone(avocat.getTelephone())
         .dateAfficheeIso(affichee.toString())
         .build();
    }

    public ConsultationDetailResponse toDetail(Consultation consultation) {
        Avocat avocat = consultation.getAvocat();
        LocalDateTime affichee = dateAffichee(consultation);

        return ConsultationDetailResponse.builder()
            .id(consultation.getId())
            .avocatNom(avocat.getFullName())
            .avocatInitiales(initiales(avocat.getFullName()))
            .specialite(specialiteOf(avocat))
            .date(affichee.format(DATE_FORMAT))
            .heure(affichee.format(HEURE_FORMAT))
            .statut(consultation.getStatut().name())
            .mode(consultation.getModeConsultation())
            .flowType(consultation.getFlowType())
            .eligibilite(consultation.getEligibilite())
            .typePersonne(consultation.getTypePersonne())
            .mission(consultation.getMission())
            .attentes(consultation.getAttentes())
            .urgent(consultation.getUrgent())
            .situation(consultation.getSituation())
            .nomComplet(consultation.getNomComplet())
            .telephone(consultation.getTelephone())
            .email(consultation.getEmail())
            .ville(consultation.getVille())
            .contactPreference(consultation.getContactPreference())
            .build();
    }

    // Once the avocat confirms and sets a real rendez-vous, show that.
    // Until then, there's no appointment yet — fall back to the demande date
    // rather than pretending a slot exists.
    private LocalDateTime dateAffichee(Consultation consultation) {
        boolean confirmee = consultation.getStatut() == StatutConsultationEnum.CONFIRMEE;
        return (confirmee && consultation.getDateRendezVous() != null)
            ? consultation.getDateRendezVous()
            : consultation.getCreatedAt();
    }

    public ConsultationAvocatSummaryResponse toAvocatSummary(Consultation consultation) {
        return ConsultationAvocatSummaryResponse.builder()
            .id(consultation.getId())
            .nomComplet(consultation.getNomComplet())
            .telephone(consultation.getTelephone())
            .email(consultation.getEmail())
            .ville(consultation.getVille())
            .typePersonne(consultation.getTypePersonne())
            .mission(consultation.getMission())
            .situation(consultation.getSituation())
            .attentes(consultation.getAttentes())
            .urgent(consultation.getUrgent())
            .statut(consultation.getStatut())
            .createdAt(consultation.getCreatedAt())
            .build();
    }

    private String specialiteOf(Avocat avocat) {
        return avocat.getSpecialites().stream()
            .findFirst()
            .map(SpecialiteDroit::getNom)
            .orElse("Général");
    }

    private String initiales(String fullName) {
        if (fullName == null || fullName.isBlank()) return "??";
        StringBuilder sb = new StringBuilder();
        for (String part : fullName.trim().split("\\s+")) {
            if (!part.isEmpty()) sb.append(Character.toUpperCase(part.charAt(0)));
            if (sb.length() >= 2) break;
        }
        return sb.toString();
    }
}