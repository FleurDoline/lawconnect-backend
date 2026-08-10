package org.arited.lawconnect.core.controller;

import lombok.RequiredArgsConstructor;
import org.arited.lawconnect.core.dtos.Response.NotificationResponse;
import org.arited.lawconnect.core.dtos.Response.UnreadCountResponse;
import org.arited.lawconnect.core.entities.User;
import org.arited.lawconnect.core.services.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        Page<NotificationResponse> result = notificationService.getForUser(
            user.getUserId(), unreadOnly, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> unreadCount(@AuthenticationPrincipal User user) {
        long count = notificationService.unreadCount(user.getUserId());
        return ResponseEntity.ok(UnreadCountResponse.builder().count(count).build());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, @AuthenticationPrincipal User user) {
        notificationService.markAsRead(id, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user.getUserId());
        return ResponseEntity.noContent().build();
    }
}