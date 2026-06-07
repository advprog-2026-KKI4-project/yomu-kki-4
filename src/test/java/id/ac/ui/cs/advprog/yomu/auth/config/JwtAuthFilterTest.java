package id.ac.ui.cs.advprog.yomu.auth.config;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class JwtAuthFilterTest {

    private JwtUtil jwtUtil;
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        String base64Secret = Base64.getEncoder().encodeToString(
                "0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(jwtUtil, "secret", base64Secret);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 60_000L);
        jwtAuthFilter = new JwtAuthFilter(jwtUtil);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternalSetsAuthenticationForValidToken() throws Exception {
        User user = User.builder()
                .id(10L)
                .email("filter@example.com")
                .role("STUDENT")
                .build();

        String token = jwtUtil.generateToken(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilter(request, response, chain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("filter@example.com", authentication.getName());
        assertEquals("ROLE_STUDENT", authentication.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void testDoFilterInternalSkipsAuthenticationWithoutBearerHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternalIgnoresMalformedToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer malformed-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternalExtractsTokenFromCookie() throws Exception {
        User user = User.builder()
                .id(20L)
                .email("cookie@example.com")
                .role("STUDENT")
                .build();
        String token = jwtUtil.generateToken(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        jakarta.servlet.http.Cookie jwtCookie = new jakarta.servlet.http.Cookie("jwt", token);
        request.setCookies(jwtCookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilter(request, response, chain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("cookie@example.com", authentication.getName());
        assertEquals("ROLE_STUDENT", authentication.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void testDoFilterInvalidTokenDoesNotSetAuthentication() throws Exception {
        User user = User.builder()
                .id(30L)
                .email("invalid@example.com")
                .role("STUDENT")
                .build();
        String token = jwtUtil.generateToken(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // Create a new filter with a shorter expiration so isTokenValid returns false
        JwtUtil shortLivedJwtUtil = new JwtUtil();
        String base64Secret = Base64.getEncoder().encodeToString(
                "0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(shortLivedJwtUtil, "secret", base64Secret);
        ReflectionTestUtils.setField(shortLivedJwtUtil, "jwtExpiration", 1L); // 1ms expiration

        User anotherUser = User.builder()
                .id(31L)
                .email("expired@example.com")
                .role("STUDENT")
                .build();
        String expiredToken = shortLivedJwtUtil.generateToken(anotherUser);

        // Use the original filter but with expired token - isTokenValid should return false
        MockHttpServletRequest request2 = new MockHttpServletRequest();
        request2.addHeader("Authorization", "Bearer " + expiredToken);
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        MockFilterChain chain2 = new MockFilterChain();

        jwtAuthFilter.doFilter(request2, response2, chain2);

        // The token is expired for jwtUtil, so isTokenValid should fail
        // Since userIdentity (email) matches but validity check fails, no auth set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterSkipsWhenCookiesPresentButNoJwtCookie() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        jakarta.servlet.http.Cookie otherCookie = new jakarta.servlet.http.Cookie("session", "abc123");
        request.setCookies(otherCookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterSetsAuthWhenRoleIsNull() throws Exception {
        User user = User.builder()
                .id(40L)
                .email("norole@example.com")
                .role(null)
                .build();
        String token = jwtUtil.generateToken(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilter(request, response, chain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("ROLE_STUDENT", authentication.getAuthorities().iterator().next().getAuthority());
    }
}
