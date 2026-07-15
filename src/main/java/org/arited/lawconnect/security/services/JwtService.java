package org.arited.lawconnect.security.services;

import org.arited.lawconnect.core.entities.User;
import org.arited.lawconnect.security.models.UserPrincipal;
import org.arited.lawconnect.security.utils.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    public boolean isTokenValid(String token, User user) {
        String email = extractEmail(token);
        return email.equals(user.getEmail()) && !isTokenExpired(token);
    }

    private String buildAccessToken(String email, String role, String fullName) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .subject(email)
            .claim("roles", "ROLE_" + role)
            .claim("fullName", fullName)
            .issuedAt(new Date(now))
            .expiration(new Date(now + jwtProperties.accessTokenExpiration()))
            .signWith(getSigningKey())
            .compact();
    }

    public String generateAccessToken(User user) {
    return buildAccessToken(user.getEmail(), user.getRole().name(), user.getFullName());
}

public String generateAccessToken(UserPrincipal principal) {
    return buildAccessToken(principal.getEmail(), principal.getRole().name(), principal.getFullName());
}

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}