package org.arited.lawconnect.security.utils;

import org.arited.lawconnect.core.entities.User;
import org.arited.lawconnect.core.repositories.UserRepository;
import org.arited.lawconnect.security.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final Set<String> BYPASS_PATHS = Set.of(
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/refresh",
        "/api/auth/logout"
    );

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain chain) throws ServletException, IOException {

        if (BYPASS_PATHS.contains(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = extractToken(request);
        if (jwt == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String email;
            try {
                email = jwtService.extractEmail(jwt);
            } catch (Exception e) {
                log.warn("JWT invalide ou expiré : {}", e.getMessage());
                sendUnauthorized(response, "Token invalide ou expiré");
                return;
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByEmail(email).orElse(null);
                if (user == null) {
                    sendUnauthorized(response, "Utilisateur introuvable");
                    return;
                }
                if (!jwtService.isTokenValid(jwt, user)) {
                    sendUnauthorized(response, "Token expiré");
                    return;
                }

                org.arited.lawconnect.security.models.UserPrincipal principal =
                    org.arited.lawconnect.security.models.UserPrincipal.create(user);

                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Erreur inattendue dans le filtre JWT : {}", e.getMessage(), e);
            sendUnauthorized(response, "Erreur lors de la validation du token");
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return CookieUtils.getCookie(request, "accessToken")
            .map(c -> c.getValue())
            .orElse(null);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\", \"status\": 401}");
    }
}