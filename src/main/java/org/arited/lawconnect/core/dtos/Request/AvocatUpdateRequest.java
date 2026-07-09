package org.arited.lawconnect.core.dtos.Request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvocatUpdateRequest {
    private String prenom;
    private String nom;
    private String telephone;
    private List<Long> specialiteIds; 
    private String bio;
    private String lienAgenda;
    private String diplome;
    private String carteProfessionnel;
    private String pieceIdentite;
    private String photo;
    private String adresseCabinet;
    private String ville;
    @DecimalMin(value = "0.0", inclusive = false, message = "Le tarif doit être positif")
    private BigDecimal tarif;
    @Min(value = 0, message = "L'expérience ne peut pas être négative")
    private Integer experience;
}