package org.arited.lawconnect.core.dtos;

public record AuthResponseDTO(
    String accessToken,
    String refreshToken,
    long expiresIn
) {}