package org.arited.lawconnect.core.controllers;

import org.arited.lawconnect.core.dtos.AuthResponseDTO;
import org.arited.lawconnect.core.dtos.GoogleLoginRequestDTO;
import org.arited.lawconnect.core.dtos.LoginRequestDTO;
import org.arited.lawconnect.core.dtos.RefreshTokenRequestDTO;
import org.arited.lawconnect.core.dtos.RegisterRequestDTO;
import org.arited.lawconnect.core.dtos.RegisterResponseDTO;
import org.arited.lawconnect.core.dtos.VerifyOtpRequestDTO;
import org.arited.lawconnect.core.dtos.ResendOtpRequestDTO;
import org.arited.lawconnect.core.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponseDTO> verifyOtp(@Valid @RequestBody VerifyOtpRequestDTO request) {
        return ResponseEntity.ok(authService.verifyOtp(request.email(), request.code()));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<Void> resendOtp(@Valid @RequestBody ResendOtpRequestDTO request) {
        authService.resendOtp(request.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponseDTO> googleLogin(@Valid @RequestBody GoogleLoginRequestDTO request) {
        return ResponseEntity.ok(authService.googleLogin(request));
    }
}