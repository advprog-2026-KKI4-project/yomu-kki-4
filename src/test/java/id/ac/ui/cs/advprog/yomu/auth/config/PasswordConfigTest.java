package id.ac.ui.cs.advprog.yomu.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordConfigTest {

    @Test
    void passwordEncoder_shouldReturnBCryptEncoder() {
        PasswordConfig config = new PasswordConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        assertNotNull(encoder);
        String encoded = encoder.encode("password123");
        assertTrue(encoded.startsWith("$2a$") || encoded.startsWith("$2b$") || encoded.startsWith("$2y$"));
        assertTrue(encoder.matches("password123", encoded));
    }
}