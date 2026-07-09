package org.arited.lawconnect.core.repositories;

import org.arited.lawconnect.core.entities.SpecialiteDroit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialiteDroitRepository extends JpaRepository<SpecialiteDroit, Long> {
    Optional<SpecialiteDroit> findByNom(String nom);
}
