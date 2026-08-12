package org.arited.lawconnect.core.services;

import lombok.RequiredArgsConstructor;
import org.arited.lawconnect.core.entities.User;
import org.arited.lawconnect.core.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public void generateAndSendOtp(User user) {
        String otp = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), user.getPrenom(), otp);
    }

    public void verifyOtp(String email, String code) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (user.getOtpCode() == null || user.getOtpExpiry() == null) {
            throw new RuntimeException("Aucun code en attente pour cet utilisateur");
        }
        if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            throw new RuntimeException("Code expiré, veuillez en redemander un");
        }
        if (!user.getOtpCode().equals(code)) {
            throw new RuntimeException("Code invalide");
        }

        user.setEmailVerified(true);
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
    }

    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (user.isEmailVerified()) {
            throw new RuntimeException("Ce compte est déjà vérifié");
        }

        generateAndSendOtp(user);
    }
}