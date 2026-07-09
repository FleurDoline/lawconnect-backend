package org.arited.lawconnect.security.services;

import org.arited.lawconnect.cache.context.SessionContext;
import org.arited.lawconnect.cache.services.CacheService;
import org.arited.lawconnect.core.entities.User;
import org.arited.lawconnect.core.exceptions.AppException;
import org.arited.lawconnect.security.entities.AppSession;
import org.arited.lawconnect.security.repositories.SessionRepository;
import org.arited.lawconnect.security.utils.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    private final SessionRepository sessionRepository;
    private final JwtProperties jwtProperties;
    private final CacheService cacheService;

    public AppSession createSession(User user, String accessToken, String refreshToken) {
        AppSession session = new AppSession();
        Timestamp now = Timestamp.from(Instant.now());
        session.setUserId(user.getUserId());
        session.setToken(accessToken);
        session.setRefreshToken(refreshToken);
        session.setStartAt(now);
        session.setExpiresAt(new Timestamp(now.getTime() + jwtProperties.refreshTokenExpiration()));
        session.setRevoked(false);
        return sessionRepository.save(session);
    }

    public AppSession getCurrentSession() {
        AppSession session = cacheService.getCurrentSession();
        if (session == null) {
            String token = SessionContext.getToken();
            if (token != null && !token.isBlank()) {
                session = sessionRepository.findByToken(token)
                    .orElseThrow(() -> new AppException("Session introuvable", HttpStatus.UNAUTHORIZED));
                cacheService.putSession(token, session);
            }
        }
        if (session != null && (session.isRevoked() ||
            session.getExpiresAt().before(Timestamp.from(Instant.now())))) {
            throw new AppException("Session invalide ou expirée", HttpStatus.UNAUTHORIZED);
        }
        return session;
    }

    @Transactional
    public void verifyExpiration(AppSession session) {
        if (session.isRevoked()) {
            throw new AppException("Refresh token révoqué, veuillez vous reconnecter", HttpStatus.UNAUTHORIZED);
        }
        if (session.getExpiresAt().before(Timestamp.from(Instant.now()))) {
            session.setRevoked(true);
            sessionRepository.save(session);
            throw new AppException("Refresh token expiré, veuillez vous reconnecter", HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public void revokeAllUserSessions(User user) {
        sessionRepository.deleteAllByUserId(user.getUserId());
        log.info("Toutes les sessions révoquées pour userId={}", user.getUserId());
    }
}