package org.arited.lawconnect.core.dtos.Response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NoteMoyenneResponse {
    private Double moyenne; // ex: 4.2, peut être null si aucun avis
    private Long nombreAvis;
}
