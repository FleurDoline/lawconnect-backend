package org.arited.lawconnect.core.dtos.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.arited.lawconnect.core.enums.StatutConsultationEnum;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationAvocatSummaryResponse {
    private Long id;
    private String nomComplet;
    private String telephone;
    private String email;
    private String ville;
    private String typePersonne;
    private String mission;
    private String situation;
    private List<String> attentes;
    private String urgent;
    private StatutConsultationEnum statut;
    private LocalDateTime createdAt;
}