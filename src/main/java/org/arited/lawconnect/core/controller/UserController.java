package org.arited.lawconnect.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arited.lawconnect.core.dtos.Request.ChangePasswordRequest;
import org.arited.lawconnect.core.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Actions generiques sur les utilisateurs")
public class UserController {

    private final UserService userService;

    @PutMapping("/{id}/password")
    @Operation(summary = "Changer le mot de passe d'un utilisateur")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("PUT /api/v1/users/{}/password", id);
        userService.changePassword(id, request);
        return ResponseEntity.ok().build();
    }
}
