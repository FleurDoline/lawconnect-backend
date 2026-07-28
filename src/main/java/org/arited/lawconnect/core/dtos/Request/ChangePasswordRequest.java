package org.arited.lawconnect.core.dtos.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

    @NotBlank(message = "Le mot de passe actuel est requis")
    private String currentPassword;

    @NotBlank(message = "Le nouveau mot de passe est requis")
    @Size(min = 8, message = "Le nouveau mot de passe doit contenir au moins 8 caracteres")
    private String newPassword;

    @NotBlank(message = "La confirmation du mot de passe est requise")
    private String confirmPassword;
}