package org.arited.lawconnect.core.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.arited.lawconnect.core.dtos.DisponibiliteDTO;
import org.arited.lawconnect.core.dtos.Request.DisponibiliteCreateRequest;
import org.arited.lawconnect.core.entities.Avocat;
import org.arited.lawconnect.core.entities.Disponibilite;
import org.arited.lawconnect.core.repositories.AvocatRepository;
import org.arited.lawconnect.core.repositories.DisponibiliteRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisponibiliteServiceImpl implements DisponibiliteService {

    private final DisponibiliteRepository disponibiliteRepository;
    private final AvocatRepository avocatRepository;

    @Override
    public List<DisponibiliteDTO> getDisponibilites(Long avocatUserId) {
        return disponibiliteRepository.findByAvocat_UserIdOrderByJour(avocatUserId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<DisponibiliteDTO> remplacerDisponibilites(Long avocatUserId, List<DisponibiliteCreateRequest> requests) {
        Avocat avocat = avocatRepository.findById(avocatUserId)
            .orElseThrow(() -> new EntityNotFoundException("Avocat introuvable : " + avocatUserId));

        // Stratégie simple : on supprime tout et on recrée (le frontend envoie l'état complet à chaque sauvegarde)
        disponibiliteRepository.deleteByAvocat_UserId(avocatUserId);

        List<Disponibilite> nouvelles = requests.stream().map(req -> {
            Disponibilite d = new Disponibilite();
            d.setAvocat(avocat);
            d.setJour(req.jour());
            d.setHeureDebut(req.heureDebut());
            d.setHeureFin(req.heureFin());
            return d;
        }).collect(Collectors.toList());

        return disponibiliteRepository.saveAll(nouvelles)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void supprimerDisponibilite(Long avocatUserId, Long disponibiliteId) {
        Disponibilite d = disponibiliteRepository.findById(disponibiliteId)
            .orElseThrow(() -> new EntityNotFoundException("Disponibilité introuvable"));

        if (!d.getAvocat().getUserId().equals(avocatUserId)) {
            throw new AccessDeniedException("Cette disponibilité ne vous appartient pas");
        }

        disponibiliteRepository.delete(d);
    }

    private DisponibiliteDTO toDTO(Disponibilite d) {
        return new DisponibiliteDTO(d.getId(), d.getJour(), d.getHeureDebut(), d.getHeureFin());
    }

}