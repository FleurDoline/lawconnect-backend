package org.arited.lawconnect.core.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestBody;

import org.arited.lawconnect.core.services.DisponibiliteService;
import org.arited.lawconnect.security.models.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.arited.lawconnect.core.dtos.DisponibiliteDTO;
import org.arited.lawconnect.core.dtos.Request.DisponibiliteCreateRequest;

import java.util.List;

@RestController
@RequestMapping("/api/avocat/disponibilites")
@RequiredArgsConstructor
public class DisponibiliteController {

    private final DisponibiliteService disponibiliteService;
    @GetMapping
    public List<DisponibiliteDTO> getMesDisponibilites(@AuthenticationPrincipal UserPrincipal principal) {
        return disponibiliteService.getDisponibilites(principal.getId());
    }

    @PostMapping
    public List<DisponibiliteDTO> enregistrerMesDisponibilites(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestBody List<DisponibiliteCreateRequest> requests) {

    return disponibiliteService.remplacerDisponibilites(principal.getId(), requests);
}
}
