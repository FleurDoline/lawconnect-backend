package org.arited.lawconnect.core.repositories;

import org.arited.lawconnect.core.entities.Consultation;
import org.arited.lawconnect.core.enums.StatutConsultationEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByClient_UserIdOrderByCreatedAtDesc(Long clientId);
    List<Consultation> findByAvocat_UserIdOrderByCreatedAtDesc(Long avocatUserId);
    List<Consultation> findByStatutAndDateRendezVousBefore(StatutConsultationEnum statut, LocalDateTime dateTime);
}