package org.arited.lawconnect.core.dtos.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record ConsultationAcceptRequest(
    @NotNull LocalDateTime dateRendezVous,
    @NotBlank
    @Pattern(regexp = "visio|telephone|cabinet", message = "modeConsultation doit être 'visio', 'telephone' ou 'cabinet'")
    String modeConsultation
) {}