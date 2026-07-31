package org.arited.lawconnect.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.arited.lawconnect.core.entities.Disponibilite;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {

    List<Disponibilite> findByAvocat_UserIdOrderByJour(Long avocatUserId);

    void deleteByAvocat_UserId(Long avocatUserId);

    @Query("SELECT d FROM Disponibilite d WHERE d.avocat.userId = :avocatId AND d.jour = :jour")
    Optional<Disponibilite> findByAvocatIdAndJour(@Param("avocatId") Long avocatId, @Param("jour") DayOfWeek jour);

    @Query("SELECT d FROM Disponibilite d WHERE d.avocat.userId = :avocatId")
    List<Disponibilite> findByAvocatId(@Param("avocatId") Long avocatId);
}