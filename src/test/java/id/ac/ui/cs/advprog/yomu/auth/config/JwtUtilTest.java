package id.ac.ui.cs.advprog.yomu.auth.config;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        String base64Secret = Base64.getEncoder().encodeToString(
                "0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(jwtUtil, "secret", base64Secret);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 60_000L);
    }

    @Test
    void testGenerateTokenContainsExpectedClaims() {
        User user = User.builder()
                .id(7L)
                .email("test@example.com")
                .role("STUDENT")
                .build();

        String token = jwtUtil.generateToken(user);

        assertNotNull(token);
        assertEquals("test@example.com", jwtUtil.extractUsername(token));
        assertEquals("STUDENT", jwtUtil.extractClaim(token, claims -> claims.get("role", String.class)));
        assertEquals(7L, jwtUtil.extractUserId(token));
        assertTrue(jwtUtil.isTokenValid(token, "test@example.com"));
    }

    @Test
    void testIsTokenValidReturnsFalseForTamperedToken() {
        User user = User.builder()
                .id(7L)
                .email("test@example.com")
                .role("STUDENT")
                .build();

        String token = jwtUtil.generateToken(user);
        String tamperedToken = token + "tampered";

        assertFalse(jwtUtil.isTokenValid(tamperedToken, "test@example.com"));
    }

    @Test
    void testIsTokenValidReturnsFalseForExpiredToken() {
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", -1L);

        User user = User.builder()
                .id(7L)
                .email("test@example.com")
                .role("STUDENT")
                .build();

        String token = jwtUtil.generateToken(user);

        assertFalse(jwtUtil.isTokenValid(token, "test@example.com"));
    }
}
