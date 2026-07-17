package org.arited.lawconnect.core.dtos.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConsultationAcceptRequest(
    @NotNull LocalDateTime dateRendezVous,
    @NotBlank String modeConsultation
) {}