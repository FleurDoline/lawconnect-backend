package org.arited.lawconnect.core.controllers;

import lombok.RequiredArgsConstructor;
import org.arited.lawconnect.core.dtos.Request.NotificationPreferenceRequest;
import org.arited.lawconnect.core.dtos.Response.NotificationPreferenceResponse;
import org.arited.lawconnect.core.entities.User;
import org.arited.lawconnect.core.services.NotificationPreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications/preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;

    @GetMapping
    public ResponseEntity<NotificationPreferenceResponse> getPreferences(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(preferenceService.getForUser(user.getUserId()));
    }

    @PutMapping
    public ResponseEntity<NotificationPreferenceResponse> updatePreferences(
            @AuthenticationPrincipal User user,
            @RequestBody NotificationPreferenceRequest request) {
        return ResponseEntity.ok(preferenceService.update(user.getUserId(), request));
    }
}
