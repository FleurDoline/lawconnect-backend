package org.arited.lawconnect.core.dtos.Response;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import org.arited.lawconnect.core.enums.StatutAvocatEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvocatSummaryResponse {
    private Long id;
    private String prenom;
    private String nom;
    private List<String> specialites;
    private String ville;
    private BigDecimal tarif;
    private BigDecimal noteMoyenne;
    private String photo;
    private StatutAvocatEnum statut;
    private String telephone;
    private String bio;
    private Integer experience;
}