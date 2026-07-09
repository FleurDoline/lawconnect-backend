package org.arited.lawconnect.core.repositories;

import org.arited.lawconnect.core.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {


    @Query("SELECT c FROM Client c WHERE c.userId = :userId")
    Optional<Client> findByUserId(@Param("userId") Long userId);

    boolean existsByUserId(Long userId);

    @Query("SELECT c FROM Client c WHERE c.email = :email")
    Optional<Client> findByEmail(@Param("email") String email);
}