package org.arited.lawconnect.core.dtos.Request;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DisponibiliteCreateRequest(
    DayOfWeek jour,
    LocalTime heureDebut,
    LocalTime heureFin
) {}