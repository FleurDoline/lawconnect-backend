package org.arited.lawconnect.core.dtos.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.arited.lawconnect.core.enums.CycleEnum;
import org.arited.lawconnect.core.enums.FormuleEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AbonnementCheckoutRequest {

    @NotNull(message = "L'avocat est obligatoire")
    private Long avocatId;

    @NotNull(message = "La formule est obligatoire")
    private FormuleEnum formule;

    @NotNull(message = "Le cycle est obligatoire")
    private CycleEnum cycle;

    @NotNull(message = "Le montant est obligatoire")
    private BigDecimal montant;

    @NotBlank(message = "Le canal de paiement est obligatoire (ex: cm.mtn, cm.orange)")
    private String channel;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String phone;
}
