package org.arited.lawconnect.core.repositories;

import org.arited.lawconnect.core.entities.EtCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CityRepository extends JpaRepository<EtCity, Long> {

    @Query(value = """
        SELECT * FROM et_city c
        WHERE c.active = true
        AND unaccent(c.city_name) ILIKE unaccent(CONCAT(:prefix, '%'))
        ORDER BY c.city_name ASC
        LIMIT 10
        """, nativeQuery = true)
    List<EtCity> findByCityNameStartingWith(@Param("prefix") String prefix);
}