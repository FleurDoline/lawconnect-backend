package org.arited.lawconnect.core.dtos.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class NotificationPreferenceRequest {
    private boolean email;
    private boolean sms;
    private boolean push;
    private boolean lettreInformation;
}