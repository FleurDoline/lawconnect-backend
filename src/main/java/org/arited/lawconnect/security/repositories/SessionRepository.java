package org.arited.lawconnect.security.repositories;

import org.arited.lawconnect.security.entities.AppSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<AppSession, Long> {
    Optional<AppSession> findByRefreshToken(String refreshToken);
    Optional<AppSession> findByToken(String token);
    void deleteAllByUserId(Long userId);
}