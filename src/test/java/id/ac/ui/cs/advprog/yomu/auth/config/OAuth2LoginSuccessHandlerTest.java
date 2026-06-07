package id.ac.ui.cs.advprog.yomu.auth.config;

import tools.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.yomu.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomu.auth.service.AuthService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2LoginSuccessHandlerTest {

    @Mock
    private AuthService authService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @InjectMocks
    private OAuth2LoginSuccessHandler handler;

    @Test
    void onAuthenticationSuccess_shouldRedirectToDashboard() throws ServletException, IOException {
        Authentication authentication = mock(Authentication.class);
        OAuth2User oauth2User = mock(OAuth2User.class);
        when(authentication.getPrincipal()).thenReturn(oauth2User);

        AuthResponse authResponse = AuthResponse.builder()
                .token("jwt-token")
                .userId(1L)
                .username("googleuser")
                .role("STUDENT")
                .message("Google login successful")
                .build();
        when(authService.loginWithGoogle(oauth2User)).thenReturn(authResponse);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(httpCookieOAuth2AuthorizationRequestRepository).removeAuthorizationRequestCookies(request, response);
        assertEquals("/dashboard", response.getRedirectedUrl());
    }

    @Test
    void onAuthenticationSuccess_shouldReturnUnauthorizedForNonOAuth2Principal() throws ServletException, IOException {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("not-an-oauth2-user");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication);

        assertEquals(401, response.getStatus());
        verify(authService, never()).loginWithGoogle(any());
    }
}