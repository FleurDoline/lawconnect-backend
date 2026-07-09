package org.arited.lawconnect.core.repositories;

import org.arited.lawconnect.core.entities.Admin;
import org.arited.lawconnect.core.enums.AccesEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    Optional<Admin> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Admin> findByNiveauAcces(AccesEnum niveauAcces);
}