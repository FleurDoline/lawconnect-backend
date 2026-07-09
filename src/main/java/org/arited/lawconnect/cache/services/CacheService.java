package org.arited.lawconnect.cache.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.arited.lawconnect.cache.context.SessionContext;
import org.arited.lawconnect.security.entities.AppSession;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private final Cache<String, AppSession> sessionCache;

    public CacheService() {
        sessionCache = Caffeine.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(500)
            .build();
    }

    public void putSession(String token, AppSession session) {
        sessionCache.put(token, session);
    }

    public AppSession getSession(String token) {
        return sessionCache.getIfPresent(token);
    }

    public AppSession getCurrentSession() {
        String token = SessionContext.getToken();
        return (token != null && !token.isBlank()) ? getSession(token) : null;
    }

    public void evictSession(String token) {
        sessionCache.invalidate(token);
    }
}