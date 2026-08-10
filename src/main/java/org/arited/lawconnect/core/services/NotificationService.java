package org.arited.lawconnect.core.services;

import org.arited.lawconnect.core.dtos.Response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    Page<NotificationResponse> getForUser(Long userId, boolean unreadOnly, Pageable pageable);

    long unreadCount(Long userId);

    void markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    void create(Long destinataireId, org.arited.lawconnect.core.enums.NotificationType type,
                String titre, String message, String lien);
}