package org.arited.lawconnect.core.dtos.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.arited.lawconnect.core.enums.NotificationType;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String titre;
    private String message;
    private String lien;
    private boolean lu;
    private LocalDateTime dateCreation;
}
