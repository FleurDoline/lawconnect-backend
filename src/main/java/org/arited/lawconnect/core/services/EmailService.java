package org.arited.lawconnect.core.services;

public interface EmailService {
    void sendLienAgenda(String toEmail, String clientNom, String avocatNom, String lienAgenda);
}