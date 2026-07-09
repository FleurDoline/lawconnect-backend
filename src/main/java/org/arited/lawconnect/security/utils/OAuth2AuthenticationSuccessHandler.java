package org.arited.lawconnect.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.arited.lawconnect.core.entities.User;
import org.arited.lawconnect.core.exceptions.AppException;
import org.arited.lawconnect.core.repositories.UserRepository;
import org.arited.lawconnect.security.models.UserPrincipal;
import org.arited.lawconnect.security.repositories.HttpCookieOAuth2AuthorizationRequestRepository;
import org.arited.lawconnect.security.services.JwtService;
import org.arited.lawconnect.security.services.SessionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieRepo;
    private final JwtProperties jwtProperties;

    @Value("${app.oauth2.user-redirect-uri}")
    private String userRedirectUri;

    @Value("${app.oauth2.admin-redirect-uri}")
    private String adminRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication)
            throws IOException {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findByEmail(principal.getEmail())
                .orElseThrow(() -> new AppException(
                    "Utilisateur introuvable", HttpStatus.NOT_FOUND));

        // Generate our own JWT pair after Google confirmed the identity
        String accessToken  = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken();
        sessionService.createSession(user, accessToken, refreshToken);

        // Set tokens as HttpOnly cookies
        response.addCookie(CookieUtils.getAccessTokenCookie(
                accessToken,
                jwtProperties.accessTokenExpiration() / 1000));
        response.addCookie(CookieUtils.getRefreshTokenCookie(
                refreshToken,
                jwtProperties.refreshTokenExpiration() / 1000));

        // Role-based redirect
        String targetUri = switch (user.getRole()) {
        case ADMIN -> adminRedirectUri;
        default    -> userRedirectUri;
      };

        // Also pass tokens in query params for SPAs that prefer that
        String redirectUrl = UriComponentsBuilder.fromUriString(targetUri)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request,
                                                HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        cookieRepo.removeAuthorizationRequest(request, response);
    }
}