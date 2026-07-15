package org.arited.lawconnect.core.repositories;

import org.arited.lawconnect.core.entities.EtCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EtCityRepository extends JpaRepository<EtCity, Long> {
    List<EtCity> findByActiveTrueOrderByCityNameAsc();
}