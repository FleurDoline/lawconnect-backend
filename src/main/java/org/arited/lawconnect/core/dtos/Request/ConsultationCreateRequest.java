package org.arited.lawconnect.core.dtos.Request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ConsultationCreateRequest {

    @NotNull
    private Long avocatId;

    @NotBlank
    private String flowType;

    private String eligibilite;

    private String typePersonne;

    private String mission;

    private List<String> attentes;

    private String urgent;

    @NotBlank
    @Size(min = 30, message = "La situation doit contenir au moins 30 caractères")
    private String situation;

    @NotBlank
    private String nomComplet;

    @NotBlank
    private String telephone;

    @NotBlank
    @Email
    private String email;

    private String ville;

    private String contactPreference;

    // Optionnel : uniquement requis si l'avocat gère ses créneaux sur la plateforme.
    // La validation conditionnelle (obligatoire ou non selon l'avocat) est faite
    // dans ConsultationServiceImpl.createConsultation().
    private LocalDateTime dateRendezVous;

    @NotBlank(message = "Veuillez choisir un mode de consultation")
    private String modeConsultation; // "visioconférence", "téléphone", ou "présentiel"
}