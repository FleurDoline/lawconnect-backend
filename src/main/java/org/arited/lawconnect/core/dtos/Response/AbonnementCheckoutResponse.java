package org.arited.lawconnect.core.dtos.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class AbonnementCheckoutResponse {
    private String status;
    private String message;
    private String abonnementReference;
    private String notchpayReference;
    private Map<?, ?> notchpayDetails;
}
