package com.lifelink.hospital_registration.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncryptionUtil {

    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordEncryptionUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean verifyPassword(String rawPassword, String encryptedPassword) {
        return passwordEncoder.matches(rawPassword, encryptedPassword);
    }
}