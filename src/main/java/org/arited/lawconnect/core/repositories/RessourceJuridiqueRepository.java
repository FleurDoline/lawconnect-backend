package org.arited.lawconnect.core.repositories;

import org.arited.lawconnect.core.entities.RessourceJuridique;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RessourceJuridiqueRepository extends JpaRepository<RessourceJuridique, Long> {

    @Query("""
        SELECT r FROM RessourceJuridique r
        JOIN FETCH r.specialite
        WHERE (:specialiteId IS NULL OR r.specialite.id = :specialiteId)
        ORDER BY r.createdAt DESC
        """)
    Page<RessourceJuridique> findAllWithFilter(@Param("specialiteId") Long specialiteId, Pageable pageable);
}
