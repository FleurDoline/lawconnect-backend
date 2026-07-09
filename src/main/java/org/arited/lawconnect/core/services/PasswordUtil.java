package org.arited.lawconnect.core.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder(10).encode("avocat123"));
    }
}
