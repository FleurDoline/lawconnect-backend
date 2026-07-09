package org.arited.lawconnect.core.dtos.Response;

import lombok.*;

import java.time.Instant;

import org.arited.lawconnect.core.enums.RoleEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponse {

    private Long id;
    private Long userId;
    private String prenom;
    private String nom;
    private String email;
    private String telephone;
    private RoleEnum role;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}