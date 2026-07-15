package org.arited.lawconnect.core.mappers;

import org.arited.lawconnect.core.dtos.Response.ConsultationDetailResponse;
import org.arited.lawconnect.core.dtos.Response.ConsultationSummaryResponse;
import org.arited.lawconnect.core.entities.Avocat;
import org.arited.lawconnect.core.entities.Consultation;
import org.arited.lawconnect.core.entities.SpecialiteDroit;
import org.springframework.stereotype.Component;

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

        return ConsultationSummaryResponse.builder()
            .id(consultation.getId())
            .avocatNom(avocat.getFullName())
            .avocatInitiales(initiales(avocat.getFullName()))
            .specialite(specialiteOf(avocat))
            .date(consultation.getCreatedAt().format(DATE_FORMAT))
            .statut(consultation.getStatut().name())
            .build();
    }

    public ConsultationDetailResponse toDetail(Consultation consultation) {
        Avocat avocat = consultation.getAvocat();

        return ConsultationDetailResponse.builder()
            .id(consultation.getId())
            .avocatNom(avocat.getFullName())
            .avocatInitiales(initiales(avocat.getFullName()))
            .specialite(specialiteOf(avocat))
            .date(consultation.getCreatedAt().format(DATE_FORMAT))
            .heure(consultation.getCreatedAt().format(HEURE_FORMAT))
            .statut(consultation.getStatut().name())
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