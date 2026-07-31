package org.arited.lawconnect.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.arited.lawconnect.core.entities.Consultation;
import org.arited.lawconnect.core.enums.StatutConsultationEnum;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    List<Consultation> findByClient_UserIdOrderByCreatedAtDesc(Long clientId);

    List<Consultation> findByAvocat_UserIdOrderByCreatedAtDesc(Long avocatUserId);

    List<Consultation> findByStatutAndDateRendezVousBefore(StatutConsultationEnum statut, LocalDateTime dateTime);

    @Query("SELECT c FROM Consultation c WHERE c.avocat.userId = :avocatId " +
           "AND c.dateRendezVous BETWEEN :debut AND :fin " +
           "AND c.statut IN :statuts")
    List<Consultation> findByAvocatIdAndDateRendezVousBetweenAndStatutIn(
            @Param("avocatId") Long avocatId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin,
            @Param("statuts") List<StatutConsultationEnum> statuts
    );
}