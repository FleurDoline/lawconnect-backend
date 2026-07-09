package org.arited.lawconnect.core.dtos.Request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.arited.lawconnect.core.enums.CycleEnum;
import org.arited.lawconnect.core.enums.FormuleEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbonnementRequest {

    @NotBlank(message = "La référence est obligatoire")
    private String reference;

    @NotNull(message = "La formule est obligatoire")
    private FormuleEnum formule;

    @NotNull(message = "Le cycle est obligatoire")
    private CycleEnum cycle;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être positif")
    private BigDecimal montant;

    @NotNull(message = "L'identifiant de l'avocat est obligatoire")
    private Long avocatId;

    private LocalDate prochainRenouvellement;
}