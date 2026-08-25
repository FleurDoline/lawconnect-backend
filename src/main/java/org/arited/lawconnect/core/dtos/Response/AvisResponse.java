package org.arited.lawconnect.core.dtos.Response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class AvisResponse {
    private Long id;
    private Long consultationId;
    private Integer note;
    private String commentaire;
    private String nomClient; // pour affichage côté avocat
    private LocalDateTime createdAt;
}
