package org.arited.lawconnect.core.dtos.Request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConsultationCreateRequest {

    @NotNull
    private Long avocatId;

    @NotBlank
    private String flowType;

    private String eligibilite;

    private String typePersonne;

    private String mission;

    private List<String> attentes;

    private String urgent;

    @NotBlank
    @Size(min = 30, message = "La situation doit contenir au moins 30 caractères")
    private String situation;

    @NotBlank
    private String nomComplet;

    @NotBlank
    private String telephone;

    @NotBlank
    @Email
    private String email;

    private String ville;

    private String contactPreference;
}