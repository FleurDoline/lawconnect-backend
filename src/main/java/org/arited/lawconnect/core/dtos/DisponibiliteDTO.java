package org.arited.lawconnect.core.dtos;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DisponibiliteDTO(
    Long id,
    DayOfWeek jour,
    LocalTime heureDebut,
    LocalTime heureFin
) {}
