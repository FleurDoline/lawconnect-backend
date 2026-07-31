package org.arited.lawconnect.core.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${resend.api.key}")
    private String apiKey;

    @Value("${resend.from.email}")
    private String fromEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendLienAgenda(String toEmail, String clientNom, String avocatNom, String lienAgenda) {
        try {
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

            Map<String, Object> body = Map.of(
                "from", "LawConnect <" + fromEmail + ">",
                "to", new String[]{ toEmail },
                "subject", "LawConnect - Maître " + avocatNom + " a accepté votre demande",
                "html", html
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);

            log.info("Email lien agenda envoyé à {} via Resend", toEmail);
        } catch (Exception e) {
            log.error("Échec de l'envoi de l'email de lien agenda à {}", toEmail, e);
        }
    }

    @Override
    public void sendConfirmationCreneauNatif(
          String toEmail, String clientNom, String avocatNom,
          LocalDateTime dateRendezVous, String modeConsultation) {
      try {
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

        Map<String, Object> body = Map.of(
            "from", "LawConnect <" + fromEmail + ">",
            "to", new String[]{ toEmail },
            "subject", "LawConnect - Maître " + avocatNom + " a confirmé votre rendez-vous",
            "html", html
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);

        log.info("Email de confirmation créneau natif envoyé à {} via Resend", toEmail);
      } catch (Exception e) {
        log.error("Échec de l'envoi de l'email de confirmation créneau natif à {}", toEmail, e);
      }
    }
}