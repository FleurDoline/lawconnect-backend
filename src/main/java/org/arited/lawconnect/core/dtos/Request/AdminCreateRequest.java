package org.arited.lawconnect.core.dtos.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.arited.lawconnect.core.enums.AccesEnum;

@Getter
@Setter
public class AdminCreateRequest {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    @NotBlank(message = "Le nom complet est obligatoire")
    private String fullName;

    private AccesEnum niveauAcces;
}