package org.arited.lawconnect.core.services;

import org.arited.lawconnect.core.dtos.AuthResponseDTO;
import org.arited.lawconnect.core.dtos.GoogleLoginRequestDTO;
import org.arited.lawconnect.core.dtos.LoginRequestDTO;
import org.arited.lawconnect.core.dtos.RefreshTokenRequestDTO;
import org.arited.lawconnect.core.dtos.RegisterRequestDTO;
import org.arited.lawconnect.core.entities.Admin;
import org.arited.lawconnect.core.entities.Avocat;
import org.arited.lawconnect.core.entities.Client;
import org.arited.lawconnect.core.entities.User;
import org.arited.lawconnect.core.enums.AuthProvider;
import org.arited.lawconnect.core.enums.RoleEnum;
import org.arited.lawconnect.core.exceptions.AppException;
import org.arited.lawconnect.core.repositories.UserRepository;
import org.arited.lawconnect.security.entities.AppSession;
import org.arited.lawconnect.security.repositories.SessionRepository;
import org.arited.lawconnect.security.services.JwtService;
import org.arited.lawconnect.security.services.SessionService;
import org.arited.lawconnect.security.utils.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final SessionService sessionService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;

    @Value("${google.client.id}")
    private String googleClientId;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException("Un compte avec cet email existe déjà", HttpStatus.BAD_REQUEST);
        }

        RoleEnum role = request.role() != null ? request.role() : RoleEnum.CLIENT;
        User user = newUserForRole(role);
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setRole(role);
        user.setProvider(AuthProvider.LOCAL);
        user.setActive(true);

        user = userRepository.save(user);

        return generateTokensAndSession(user);
    }

    private User newUserForRole(RoleEnum role) {
        return switch (role) {
            case CLIENT -> new Client();
            case AVOCAT -> new Avocat();
            case ADMIN -> new Admin();
        };
    }

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new AppException("Utilisateur introuvable", HttpStatus.NOT_FOUND));
        return generateTokensAndSession(user);
    }

    @Transactional
    public AuthResponseDTO refresh(RefreshTokenRequestDTO request) {
        AppSession session = sessionRepository.findByRefreshToken(request.refreshToken())
            .orElseThrow(() -> new AppException("Refresh token introuvable", HttpStatus.NOT_FOUND));
        sessionService.verifyExpiration(session);
        User user = userRepository.findById(session.getUserId())
            .orElseThrow(() -> new AppException("Utilisateur introuvable", HttpStatus.NOT_FOUND));
        String newAccessToken = jwtService.generateAccessToken(user);
        session.setToken(newAccessToken);
        sessionRepository.save(session);
        return new AuthResponseDTO(newAccessToken, request.refreshToken(),
            jwtProperties.accessTokenExpiration() / 1000);
    }

    @Transactional
    public void logout(String refreshToken) {
        AppSession session = sessionRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new AppException("Refresh token introuvable", HttpStatus.NOT_FOUND));
        session.setRevoked(true);
        sessionRepository.save(session);
    }

    @Transactional
    public AuthResponseDTO googleLogin(GoogleLoginRequestDTO request) {
       GoogleIdToken.Payload payload = verifyGoogleToken(request.idToken());

       String email = payload.getEmail();
       String fullName = (String) payload.get("name");
       String googleSub = payload.getSubject();

      User user = userRepository.findByEmail(email)
        .orElseGet(() -> {
            RoleEnum role = request.role() != null ? request.role() : RoleEnum.CLIENT;
            User newUser = newUserForRole(role);
            newUser.setEmail(email);
            newUser.setFullName(fullName);
            newUser.setRole(role);
            newUser.setProvider(AuthProvider.GOOGLE);
            newUser.setProviderId(googleSub);
            newUser.setActive(true);
            return userRepository.save(newUser);
        });

       return generateTokensAndSession(user);
    }

    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new AppException("Token Google invalide", HttpStatus.UNAUTHORIZED);
            }
            return idToken.getPayload();
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Erreur de vérification du token Google", HttpStatus.UNAUTHORIZED);
        }
    }

    private AuthResponseDTO generateTokensAndSession(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken();
        sessionService.createSession(user, accessToken, refreshToken);
        return new AuthResponseDTO(accessToken, refreshToken,
            jwtProperties.accessTokenExpiration() / 1000);
    }
}