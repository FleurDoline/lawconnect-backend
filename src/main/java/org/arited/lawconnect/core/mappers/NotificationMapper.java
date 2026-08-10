package org.arited.lawconnect.core.mappers;

import org.arited.lawconnect.core.dtos.Response.NotificationPreferenceResponse;
import org.arited.lawconnect.core.dtos.Response.NotificationResponse;
import org.arited.lawconnect.core.entities.Notification;
import org.arited.lawconnect.core.entities.NotificationPreference;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .type(notification.getType())
            .titre(notification.getTitre())
            .message(notification.getMessage())
            .lien(notification.getLien())
            .lu(notification.isLu())
            .dateCreation(notification.getDateCreation())
            .build();
    }

    public NotificationPreferenceResponse toResponse(NotificationPreference preference) {
        return NotificationPreferenceResponse.builder()
            .email(preference.isEmail())
            .sms(preference.isSms())
            .push(preference.isPush())
            .lettreInformation(preference.isLettreInformation())
            .build();
    }
}
