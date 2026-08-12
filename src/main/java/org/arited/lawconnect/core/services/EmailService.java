package org.arited.lawconnect.core.services;

import java.time.LocalDateTime;

public interface EmailService {
    void sendLienAgenda(String toEmail, String clientNom, String avocatNom, String lienAgenda);

    void sendConfirmationCreneauNatif(
        String toEmail, String clientNom, String avocatNom,
        LocalDateTime dateRendezVous, String modeConsultation
    );
    void sendOtpEmail(String toEmail, String prenom, String otp);
}