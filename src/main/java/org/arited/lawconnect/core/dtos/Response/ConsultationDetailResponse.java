package org.arited.lawconnect.core.dtos.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ConsultationDetailResponse {
    private Long id;
    private String avocatNom;
    private String avocatInitiales;
    private String specialite;
    private String date;
    private String heure;
    private String statut;
    private String flowType;
    private String eligibilite;
    private String typePersonne;
    private String mission;
    private List<String> attentes;
    private String urgent;
    private String situation;
    private String nomComplet;
    private String telephone;
    private String email;
    private String ville;
    private String contactPreference;
}