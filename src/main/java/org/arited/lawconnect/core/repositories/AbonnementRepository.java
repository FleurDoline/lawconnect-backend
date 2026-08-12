package org.arited.lawconnect.core.repositories;

import org.arited.lawconnect.core.entities.Abonnement;
import org.arited.lawconnect.core.enums.FormuleEnum;
import org.arited.lawconnect.core.enums.StatutPaiementEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {

    Optional<Abonnement> findByReference(String reference);

    boolean existsByReference(String reference);

    List<Abonnement> findByAvocatUserId(Long avocatUserId);

    List<Abonnement> findByStatut(StatutPaiementEnum statut);

    List<Abonnement> findByFormule(FormuleEnum formule);

    List<Abonnement> findByProchainRenouvellementBefore(LocalDate date);

    Optional<Abonnement> findByAvocatUserIdAndStatut(Long avocatUserId, StatutPaiementEnum statut);

    long countByStatut(StatutPaiementEnum statut);
}