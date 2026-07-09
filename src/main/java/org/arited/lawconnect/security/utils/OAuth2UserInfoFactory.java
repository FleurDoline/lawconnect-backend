package org.arited.lawconnect.security.utils;

import org.arited.lawconnect.core.enums.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(
            String registrationId,
            Map<String, Object> attributes) {

        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        throw new IllegalArgumentException(
            "Provider non supporté : " + registrationId
        );
    }
}