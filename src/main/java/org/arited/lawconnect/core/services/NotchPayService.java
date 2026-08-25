package org.arited.lawconnect.core.services;

import java.util.Map;

public interface NotchPayService {
    String initializePayment(String reference, java.math.BigDecimal amount, String email, String customerName, String description, String phone);
    Map<?, ?> chargeDirectPayment(String notchpayReference, String channel, String phone);
}