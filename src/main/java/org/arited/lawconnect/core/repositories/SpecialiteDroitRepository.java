package org.arited.lawconnect.core.repositories;

import org.arited.lawconnect.core.entities.SpecialiteDroit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpecialiteDroitRepository extends JpaRepository<SpecialiteDroit, Long> {
    List<SpecialiteDroit> findByNomContainingIgnoreCaseOrderByNomAsc(String nom);
}
