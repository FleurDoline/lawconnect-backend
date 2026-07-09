package org.arited.lawconnect.core.repositories;

import org.arited.lawconnect.core.entities.User;
import org.arited.lawconnect.core.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

   
    boolean existsByEmail(@Param("email") String email);

    List<User> findByRole(RoleEnum role);

    List<User> findByIsActive(boolean isActive);
}