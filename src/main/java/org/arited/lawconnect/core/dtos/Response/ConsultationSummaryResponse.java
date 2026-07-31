package org.arited.lawconnect.core.dtos.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ConsultationSummaryResponse {
    private Long id;
    private String avocatNom;
    private String avocatInitiales;
    private String specialite;
    private String date;
    private String heure;
    private String statut;
    private String mode;
    private String avocatTelephone;
    private String dateAfficheeIso;
}