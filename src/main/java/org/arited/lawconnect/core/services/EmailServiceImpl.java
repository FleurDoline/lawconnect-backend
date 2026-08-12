package org.arited.lawconnect.core.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${lawconnect.mail.from-name:LawConnect}")
    private String fromName;

    @Override
    public void sendLienAgenda(String toEmail, String clientNom, String avocatNom, String lienAgenda) {
        String html = """
            <div style="font-family: Arial, sans-serif; max-width: 560px; margin: auto;">
                <h2 style="color:#1a1a2e;">LawConnect</h2>
                <p>Bonjour %s,</p>
                <p><strong>Maître %s</strong> a accepté votre demande de consultation.</p>
                <p>Merci de choisir un créneau qui vous convient parmi ses disponibilités :</p>
                <div style="text-align:center; margin: 24px 0;">
                    <a href="%s" style="background:#1a1a2e; color:#fff; padding:12px 24px;
                       text-decoration:none; border-radius:6px; display:inline-block;">
                       Voir les créneaux disponibles
                    </a>
                </div>
                <p style="color:#666; font-size:13px;">Si le bouton ne fonctionne pas, copiez ce lien : %s</p>
                <p>L'équipe LawConnect</p>
            </div>
            """.formatted(clientNom, avocatNom, lienAgenda, lienAgenda);

        sendHtmlEmail(toEmail, "LawConnect - Maître " + avocatNom + " a accepté votre demande", html, "lien agenda");
    }

    @Override
    public void sendConfirmationCreneauNatif(
          String toEmail, String clientNom, String avocatNom,
          LocalDateTime dateRendezVous, String modeConsultation) {

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);
        DateTimeFormatter heureFormat = DateTimeFormatter.ofPattern("HH:mm");

        String dateFormatee = dateRendezVous.format(dateFormat);
        String heureFormatee = dateRendezVous.format(heureFormat);

        String html = """
            <div style="font-family: Arial, sans-serif; max-width: 560px; margin: auto;">
                <h2 style="color:#1a1a2e;">LawConnect</h2>
                <p>Bonjour %s,</p>
                <p><strong>Maître %s</strong> a accepté votre demande de consultation.</p>
                <p>Votre rendez-vous est confirmé :</p>
                <div style="background:#f5f6f8; border-radius:8px; padding:16px; margin: 20px 0;">
                    <p style="margin:4px 0;"><strong>Date :</strong> %s</p>
                    <p style="margin:4px 0;"><strong>Heure :</strong> %s</p>
                    <p style="margin:4px 0;"><strong>Mode :</strong> %s</p>
                </div>
                <p>Vous pouvez retrouver les détails dans votre espace LawConnect, rubrique "Mes consultations".</p>
                <p>L'équipe LawConnect</p>
            </div>
            """.formatted(clientNom, avocatNom, dateFormatee, heureFormatee, modeConsultation);

        sendHtmlEmail(toEmail, "LawConnect - Maître " + avocatNom + " a confirmé votre rendez-vous", html, "confirmation créneau natif");
    }

    @Override
    public void sendOtpEmail(String toEmail, String prenom, String otp) {
        String html = """
            <div style="font-family: Arial, sans-serif; max-width: 560px; margin: auto;">
                <h2 style="color:#1a1a2e;">LawConnect</h2>
                <p>Bonjour %s,</p>
                <p>Merci de vous être inscrit sur LawConnect. Voici votre code de vérification :</p>
                <div style="text-align:center; margin: 24px 0;">
                    <span style="font-size:32px; font-weight:bold; letter-spacing:8px; color:#1a1a2e;">%s</span>
                </div>
                <p style="color:#666; font-size:13px;">Ce code expire dans 10 minutes. Si vous n'avez pas demandé ce code, ignorez cet email.</p>
                <p>L'équipe LawConnect</p>
            </div>
            """.formatted(prenom, otp);

        sendHtmlEmail(toEmail, "LawConnect - Votre code de vérification", html, "OTP");
    }

    /**
     * Nombre de tentatives en cas d'échec réseau (timeout, connexion refusée, etc.).
     */
    private static final int MAX_RETRIES = 3;

    /**
     * Délai entre deux tentatives, en millisecondes.
     */
    private static final long RETRY_DELAY_MS = 2000;

    /**
     * Méthode générique d'envoi d'e-mail HTML via SMTP (JavaMailSender), avec retry
     * automatique en cas d'échec réseau ponctuel (timeout, connexion instable).
     */
    private void sendHtmlEmail(String toEmail, String subject, String htmlContent, String typeLog) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            attempt++;
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(fromName + " <" + fromEmail + ">");
                helper.setTo(toEmail);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);

                mailSender.send(message);
                log.info("Email {} envoyé à {} via SMTP Gmail (tentative {}/{})", typeLog, toEmail, attempt, MAX_RETRIES);
                return;
            } catch (MessagingException e) {
                // Erreur de préparation du message (contenu invalide) : pas la peine de réessayer.
                log.error("Échec de préparation de l'email {} pour {}", typeLog, toEmail, e);
                return;
            } catch (Exception e) {
                boolean isLastAttempt = attempt >= MAX_RETRIES;
                if (isLastAttempt) {
                    log.error("Échec définitif de l'envoi de l'email {} à {} après {} tentatives", typeLog, toEmail, attempt, e);
                } else {
                    log.warn("Échec de l'envoi de l'email {} à {} (tentative {}/{}), nouvelle tentative dans {} ms : {}",
                        typeLog, toEmail, attempt, MAX_RETRIES, RETRY_DELAY_MS, e.getMessage());
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Retry de l'envoi d'email interrompu pour {}", toEmail, ie);
                        return;
                    }
                }
            }
        }
    }
}