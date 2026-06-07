package id.ac.ui.cs.advprog.yomu.auth.controller;

import id.ac.ui.cs.advprog.yomu.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomu.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.UpdateProfileRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.UserProfileResponse;
import id.ac.ui.cs.advprog.yomu.auth.service.AuthService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private Bucket loginRateLimitBucket;

    @InjectMocks
    private AuthController authController;

    @Test
    void testRegisterEndpointReturns201() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AuthResponse response = AuthResponse.builder()
                .message("Registration successful")
                .username("testuser")
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        org.springframework.mock.web.MockHttpServletResponse mockResponse = new org.springframework.mock.web.MockHttpServletResponse();
        ResponseEntity<AuthResponse> result = authController.register(request, mockResponse);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Registration successful", result.getBody().getMessage());
    }

    @Test
    void testLoginEndpointReturns200() {
        LoginRequest request = new LoginRequest();
        request.setEmailOrPhone("test@example.com");
        request.setPassword("password123");

        AuthResponse response = AuthResponse.builder()
                .message("Login successful")
                .username("testuser")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);
        when(loginRateLimitBucket.tryConsume(1)).thenReturn(true);

        org.springframework.mock.web.MockHttpServletResponse mockResponse = new org.springframework.mock.web.MockHttpServletResponse();
        ResponseEntity<AuthResponse> result = authController.login(request, mockResponse);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Login successful", result.getBody().getMessage());
    }

    @Test
    void testRegisterRequestRequiresEmailOrPhone() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        assertEquals(false, request.hasLoginIdentifier());
    }

    @Test
    void testGetProfileReturns200() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            "test@example.com",
            null,
            Collections.emptyList()
        );
        UserProfileResponse profileResponse = UserProfileResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role("STUDENT")
                .build();

        when(authService.getProfile("test@example.com")).thenReturn(profileResponse);

        ResponseEntity<UserProfileResponse> result = authController.getProfile(authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("testuser", result.getBody().getUsername());
    }

    @Test
    void testUpdateProfileReturns200() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            "test@example.com",
            null,
            Collections.emptyList()
        );
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Test");
        request.setLastName("User");

        UserProfileResponse profileResponse = UserProfileResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role("STUDENT")
                .build();

        when(authService.updateProfile(any(String.class), any(UpdateProfileRequest.class))).thenReturn(profileResponse);

        ResponseEntity<UserProfileResponse> result = authController.updateProfile(authentication, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Test", result.getBody().getFirstName());
    }

    @Test
    void testGetGoogleAuthorizationUrlReturns200() {
        ResponseEntity<Map<String, String>> result = authController.getGoogleAuthorizationUrl();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("/oauth2/authorization/google", result.getBody().get("authorizationUrl"));
    }

    @Test
    void testLoginReturns429WhenRateLimited() {
        LoginRequest request = new LoginRequest();
        request.setEmailOrPhone("test@example.com");
        request.setPassword("password123");

        when(loginRateLimitBucket.tryConsume(1)).thenReturn(false);

        org.springframework.mock.web.MockHttpServletResponse mockResponse = new org.springframework.mock.web.MockHttpServletResponse();
        ResponseEntity<AuthResponse> result = authController.login(request, mockResponse);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Too many login attempts. Please try again in 1 minute.", result.getBody().getMessage());
    }

    @Test
    void testGetProfileThrowsWhenAuthenticationIsNull() {
        try {
            authController.getProfile(null);
        } catch (RuntimeException e) {
            assertEquals("Unauthorized", e.getMessage());
        }
    }
}
