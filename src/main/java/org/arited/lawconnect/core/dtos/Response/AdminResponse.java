package org.arited.lawconnect.core.dtos.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.arited.lawconnect.core.enums.AccesEnum;

import java.time.Instant;

@Getter
@Setter
@Builder
public class AdminResponse {
    private Long userId;
    private String email;
    private String fullName;
    private String role;
    private AccesEnum niveauAcces;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean active;
}
