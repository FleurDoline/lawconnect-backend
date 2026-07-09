package org.arited.lawconnect.security.services;

import lombok.RequiredArgsConstructor;
import org.arited.lawconnect.core.entities.User;
import org.arited.lawconnect.core.enums.AuthProvider;
import org.arited.lawconnect.core.enums.RoleEnum;
import org.arited.lawconnect.core.repositories.UserRepository;
import org.arited.lawconnect.security.models.UserPrincipal;
import org.arited.lawconnect.security.utils.OAuth2UserInfo;
import org.arited.lawconnect.security.utils.OAuth2UserInfoFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request)
            throws OAuth2AuthenticationException {

        // 1. Fetch raw attributes from Google
        OAuth2User oAuth2User = super.loadUser(request);
        String registrationId = request
                .getClientRegistration()
                .getRegistrationId(); // "google"

        // 2. Normalise into our model
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory
                .getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        if (!StringUtils.hasText(userInfo.getEmail())) {
            throw new OAuth2AuthenticationException(
                "Email introuvable chez Google"
            );
        }

        // 3. Upsert user in DB
        User user = userRepository.findByEmail(userInfo.getEmail())
                .map(existing -> updateExistingUser(existing, userInfo))
                .orElseGet(() -> createNewUser(registrationId, userInfo));

        // 4. Return our UserPrincipal with Google attributes attached
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User createNewUser(String registrationId, OAuth2UserInfo info) {
        User user = new User();
        user.setProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
        user.setProviderId(info.getId());
        user.setEmail(info.getEmail());
        user.setFullName(info.getName());
        user.setProfilePicture(info.getPicture());
        user.setRole(RoleEnum.CLIENT);  
        return userRepository.save(user);
    }

    private User updateExistingUser(User user, OAuth2UserInfo info) {
        // Only sync public profile info — never touch role or password
        user.setFullName(info.getName());
        user.setProfilePicture(info.getPicture());
        return userRepository.save(user);
    }
}