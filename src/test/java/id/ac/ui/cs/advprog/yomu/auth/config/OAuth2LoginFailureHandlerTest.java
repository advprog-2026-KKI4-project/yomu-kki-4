package id.ac.ui.cs.advprog.yomu.auth.config;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OAuth2LoginFailureHandlerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @InjectMocks
    private OAuth2LoginFailureHandler handler;

    @Test
    void onAuthenticationFailure_shouldReturn401AndClearCookies() throws ServletException, IOException {
        AuthenticationException exception = mock(AuthenticationException.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationFailure(request, response, exception);

        assertEquals(401, response.getStatus());
        assertEquals("application/json", response.getContentType());
        verify(httpCookieOAuth2AuthorizationRequestRepository).removeAuthorizationRequestCookies(request, response);
    }
}