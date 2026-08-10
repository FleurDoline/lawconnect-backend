package org.arited.lawconnect.core.services;

import lombok.RequiredArgsConstructor;
import org.arited.lawconnect.core.dtos.Response.NotificationResponse;
import org.arited.lawconnect.core.entities.Notification;
import org.arited.lawconnect.core.entities.User;
import org.arited.lawconnect.core.enums.NotificationType;
import org.arited.lawconnect.core.exceptions.ResourceNotFoundException;
import org.arited.lawconnect.core.mappers.NotificationMapper;
import org.arited.lawconnect.core.repositories.NotificationRepository;
import org.arited.lawconnect.core.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public Page<NotificationResponse> getForUser(Long userId, boolean unreadOnly, Pageable pageable) {
        Page<Notification> page = unreadOnly
            ? notificationRepository.findByDestinataireUserIdAndLuFalseOrderByDateCreationDesc(userId, pageable)
            : notificationRepository.findByDestinataireUserIdOrderByDateCreationDesc(userId, pageable);
        return page.map(notificationMapper::toResponse);
    }

    @Override
    public long unreadCount(Long userId) {
        return notificationRepository.countByDestinataireUserIdAndLuFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification introuvable"));
        if (!notification.getDestinataire().getUserId().equals(userId)) {
            throw new AccessDeniedException("Accès refusé");
        }
        notification.setLu(true);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    @Override
    @Transactional
    public void create(Long destinataireId, NotificationType type, String titre, String message, String lien) {
        User destinataire = userRepository.findById(destinataireId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        Notification notification = Notification.builder()
            .destinataire(destinataire)
            .type(type)
            .titre(titre)
            .message(message)
            .lien(lien)
            .build();

        notificationRepository.save(notification);
    }
}
