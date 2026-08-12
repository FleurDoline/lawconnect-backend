package org.arited.lawconnect.core.dtos;

import org.arited.lawconnect.core.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format de l'email invalide")
    String email,

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit faire au moins 6 caractères")
    String password,

    @NotBlank(message = "Le prénom est obligatoire")
    String prenom,

    @NotBlank(message = "Le nom est obligatoire")
    String nom,

    RoleEnum role
) {}