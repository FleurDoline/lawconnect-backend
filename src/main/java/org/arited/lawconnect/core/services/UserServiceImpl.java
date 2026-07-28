package org.arited.lawconnect.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arited.lawconnect.core.dtos.Request.ChangePasswordRequest;
import org.arited.lawconnect.core.entities.User;
import org.arited.lawconnect.core.exceptions.ResourceNotFoundException;
import org.arited.lawconnect.core.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        log.info("Changing password for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec id=" + userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Le mot de passe actuel est incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe et la confirmation ne correspondent pas");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit etre different de l'ancien");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for userId={}", userId);
    }
}