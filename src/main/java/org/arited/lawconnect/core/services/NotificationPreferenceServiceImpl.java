package org.arited.lawconnect.core.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.arited.lawconnect.core.dtos.Request.NotificationPreferenceRequest;
import org.arited.lawconnect.core.dtos.Response.NotificationPreferenceResponse;
import org.arited.lawconnect.core.entities.NotificationPreference;
import org.arited.lawconnect.core.entities.User;
import org.arited.lawconnect.core.mappers.NotificationMapper;
import org.arited.lawconnect.core.repositories.NotificationPreferenceRepository;
import org.arited.lawconnect.core.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationPreferenceResponse getForUser(Long userId) {
        NotificationPreference pref = getOrCreateDefault(userId);
        return notificationMapper.toResponse(pref);
    }

    @Override
    @Transactional
    public NotificationPreferenceResponse update(Long userId, NotificationPreferenceRequest request) {
        NotificationPreference pref = getOrCreateDefault(userId);
        pref.setEmail(request.isEmail());
        pref.setSms(request.isSms());
        pref.setPush(request.isPush());
        pref.setLettreInformation(request.isLettreInformation());
        return notificationMapper.toResponse(pref);
    }

    @Transactional
    protected NotificationPreference getOrCreateDefault(Long userId) {
        return preferenceRepository.findByUserUserId(userId)
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
                NotificationPreference pref = NotificationPreference.builder()
                    .user(user)
                    .build();
                return preferenceRepository.save(pref);
            });
    }
}
