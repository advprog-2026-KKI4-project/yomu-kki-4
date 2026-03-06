package id.ac.ui.cs.advprog.yomu.auth.controller;

import id.ac.ui.cs.advprog.yomu.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomu.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomu.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

        @Mock
    private AuthService authService;

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

        ResponseEntity<AuthResponse> result = authController.register(request);

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

        ResponseEntity<AuthResponse> result = authController.login(request);

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
}
