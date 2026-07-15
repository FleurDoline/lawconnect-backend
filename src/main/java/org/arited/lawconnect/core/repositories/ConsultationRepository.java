package org.arited.lawconnect.core.repositories;

import org.arited.lawconnect.core.entities.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByClient_UserIdOrderByCreatedAtDesc(Long clientId);
}