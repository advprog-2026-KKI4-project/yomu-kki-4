package id.ac.ui.cs.advprog.yomu.auth.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Test
    void passwordEncoderBean_shouldBeAvailable() {
        assertNotNull(passwordEncoder);
        String encoded = passwordEncoder.encode("test");
        assertTrue(passwordEncoder.matches("test", encoded));
    }

    @Test
    void corsConfigurationSource_shouldBeConfigured() {
        assertNotNull(corsConfigurationSource);
    }
}