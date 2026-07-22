package org.arited.lawconnect.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arited.lawconnect.core.entities.Consultation;
import org.arited.lawconnect.core.enums.StatutConsultationEnum;
import org.arited.lawconnect.core.repositories.ConsultationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultationSchedulerService {

    private final ConsultationRepository consultationRepository;

    // Toutes les heures — passe automatiquement en TERMINEE les
    // consultations CONFIRMEE dont la date de rendez-vous est dépassée.
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void marquerConsultationsTerminees() {
        LocalDateTime maintenant = LocalDateTime.now();

        List<Consultation> expirees = consultationRepository
            .findByStatutAndDateRendezVousBefore(StatutConsultationEnum.CONFIRMEE, maintenant);

        if (expirees.isEmpty()) {
            return;
        }

        expirees.forEach(c -> c.setStatut(StatutConsultationEnum.TERMINEE));
        consultationRepository.saveAll(expirees);

        log.info("{} consultation(s) passée(s) automatiquement à TERMINEE", expirees.size());
    }
}
