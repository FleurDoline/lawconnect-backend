package org.arited.lawconnect.core.dtos.Request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AvisCreateRequest {

    @NotNull
    private Long consultationId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer note;

    private String commentaire; // optionnel
}