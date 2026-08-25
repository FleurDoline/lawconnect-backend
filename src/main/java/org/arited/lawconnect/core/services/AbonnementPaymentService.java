package org.arited.lawconnect.core.services;

import org.arited.lawconnect.core.dtos.Request.AbonnementCheckoutRequest;
import org.arited.lawconnect.core.dtos.Response.AbonnementCheckoutResponse;

public interface AbonnementPaymentService {
    AbonnementCheckoutResponse checkout(AbonnementCheckoutRequest request);
}