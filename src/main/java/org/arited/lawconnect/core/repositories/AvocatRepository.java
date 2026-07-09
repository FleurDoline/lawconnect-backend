package org.arited.lawconnect.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.arited.lawconnect.core.entities.Avocat;
import org.arited.lawconnect.core.enums.StatutAvocatEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvocatRepository extends JpaRepository<Avocat, Long> {
    Optional<Avocat> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    Optional<Avocat> findByEmail(String email);
    List<Avocat> findByStatut(StatutAvocatEnum statut);
    Page<Avocat> findByStatut(StatutAvocatEnum statut, Pageable pageable);
    List<Avocat> findByVille(String ville);
    List<Avocat> findByValidBy(Long adminId);

    @Query("SELECT a FROM Avocat a WHERE a.statut = 'ACTIF' ORDER BY a.noteMoyenne DESC")
    List<Avocat> findTopRatedAvocats();

    @Query("SELECT a FROM Avocat a WHERE a.statut = 'VALIDE' AND (:specialites IS NULL OR EXISTS (SELECT s FROM a.specialites s WHERE s.nom IN :specialites)) AND (:ville IS NULL OR LOWER(a.ville) = LOWER(:ville))")
    Page<Avocat> findBySpecialitesAndVille(@Param("specialites") List<String> specialites, @Param("ville") String ville, Pageable pageable);
    @Query("SELECT a FROM Avocat a WHERE a.statut = 'VALIDE'")
    Page<Avocat> findAllValid(Pageable pageable);
    
    @Query("SELECT a FROM Avocat a WHERE a.statut = 'VALIDE' AND LOWER(a.ville) = LOWER(:ville)")
    Page<Avocat> findByVille(@Param("ville") String ville, Pageable pageable);
}