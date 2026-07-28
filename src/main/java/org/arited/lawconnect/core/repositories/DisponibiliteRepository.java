package org.arited.lawconnect.core.repositories;

import org.arited.lawconnect.core.entities.Disponibilite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {
    List<Disponibilite> findByAvocat_UserIdOrderByJour(Long avocatUserId);
    void deleteByAvocat_UserId(Long avocatUserId);
}
