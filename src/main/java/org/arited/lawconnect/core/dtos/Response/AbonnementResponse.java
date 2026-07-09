package org.arited.lawconnect.core.dtos.Response;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.arited.lawconnect.core.enums.CycleEnum;
import org.arited.lawconnect.core.enums.FormuleEnum;
import org.arited.lawconnect.core.enums.StatutPaiementEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AbonnementResponse {
    private Long id;
    private String reference;
    private FormuleEnum formule;
    private CycleEnum cycle;
    private BigDecimal montant;
    private StatutPaiementEnum statut;
    private LocalDate prochainRenouvellement;
    private Instant createdAt;
    private Instant updatedAt;
    private Long avocatId;
    private String avocatFullName;
    private String avocatEmail;
}