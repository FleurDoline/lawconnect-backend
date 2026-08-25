package org.arited.lawconnect.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.arited.lawconnect.core.entities.Avis;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvisRepository extends JpaRepository<Avis, Long> {

    // Vérifie si un avis existe déjà pour une consultation donnée
    boolean existsByConsultation_Id(Long consultationId);

    // Récupère l'avis d'une consultation précise (si il existe)
    Optional<Avis> findByConsultation_Id(Long consultationId);

    // Tous les avis reçus par un avocat (via la consultation liée)
    List<Avis> findByConsultation_Avocat_UserIdOrderByCreatedAtDesc(Long avocatUserId);

    // Calcul de la moyenne des notes pour un avocat
    @Query("SELECT AVG(a.note) FROM Avis a WHERE a.consultation.avocat.userId = :avocatId")
    Double findNoteMoyenneByAvocatId(@Param("avocatId") Long avocatId);

    // Nombre total d'avis pour un avocat (utile à afficher à côté de la moyenne)
    @Query("SELECT COUNT(a) FROM Avis a WHERE a.consultation.avocat.userId = :avocatId")
    Long findNombreTotalAvisByAvocatId(@Param("avocatId") Long avocatId);
}