package org.arited.lawconnect.core.services;

import org.arited.lawconnect.core.dtos.Request.NotificationPreferenceRequest;
import org.arited.lawconnect.core.dtos.Response.NotificationPreferenceResponse;

public interface NotificationPreferenceService {

    NotificationPreferenceResponse getForUser(Long userId);

    NotificationPreferenceResponse update(Long userId, NotificationPreferenceRequest request);
}
