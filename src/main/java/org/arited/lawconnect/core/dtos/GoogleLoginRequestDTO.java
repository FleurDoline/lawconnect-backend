package org.arited.lawconnect.core.dtos;
import org.arited.lawconnect.core.enums.RoleEnum;

public record GoogleLoginRequestDTO(String idToken, RoleEnum role) {}