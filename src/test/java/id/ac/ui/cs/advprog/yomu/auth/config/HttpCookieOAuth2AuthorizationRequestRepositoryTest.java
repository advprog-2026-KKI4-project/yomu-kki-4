package id.ac.ui.cs.advprog.yomu.auth.config;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import static org.junit.jupiter.api.Assertions.*;

class HttpCookieOAuth2AuthorizationRequestRepositoryTest {

    private final HttpCookieOAuth2AuthorizationRequestRepository repository =
            new HttpCookieOAuth2AuthorizationRequestRepository();

    @Test
    void loadAuthorizationRequest_shouldReturnNullWhenNoCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        OAuth2AuthorizationRequest result = repository.loadAuthorizationRequest(request);

        assertNull(result);
    }

    @Test
    void saveAuthorizationRequest_shouldStoreInCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri("https://example.com/oauth2/authorize")
                .clientId("test-client")
                .redirectUri("https://example.com/callback")
                .build();

        repository.saveAuthorizationRequest(authRequest, request, response);

        Cookie cookie = response.getCookie(HttpCookieOAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        assertNotNull(cookie);
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
    }

    @Test
    void saveAuthorizationRequest_shouldDeleteCookiesWhenAuthorizationRequestIsNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("oauth2_auth_request", "old-value"));
        request.setCookies(new Cookie("redirect_uri", "old-redirect"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        repository.saveAuthorizationRequest(null, request, response);

        assertNull(response.getCookie("oauth2_auth_request"));
    }

    @Test
    void saveAuthorizationRequest_shouldStoreRedirectUriWhenPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("redirect_uri", "https://example.com/home");
        MockHttpServletResponse response = new MockHttpServletResponse();
        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri("https://example.com/oauth2/authorize")
                .clientId("test-client")
                .redirectUri("https://example.com/callback")
                .build();

        repository.saveAuthorizationRequest(authRequest, request, response);

        Cookie redirectCookie = response.getCookie(HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME);
        assertNotNull(redirectCookie);
        assertEquals("https://example.com/home", redirectCookie.getValue());
    }

    @Test
    void removeAuthorizationRequest_shouldReturnLoadedRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri("https://example.com/oauth2/authorize")
                .clientId("test-client")
                .redirectUri("https://example.com/callback")
                .build();
        MockHttpServletResponse response = new MockHttpServletResponse();
        repository.saveAuthorizationRequest(authRequest, request, response);

        Cookie savedCookie = response.getCookie("oauth2_auth_request");
        request.setCookies(savedCookie);

        OAuth2AuthorizationRequest result = repository.removeAuthorizationRequest(request, response);

        assertNotNull(result);
        assertEquals("test-client", result.getClientId());
    }

    @Test
    void removeAuthorizationRequestCookies_shouldDeleteBothCookies() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie authCookie = new Cookie("oauth2_auth_request", "some-value");
        Cookie redirectCookie = new Cookie("redirect_uri", "some-uri");
        request.setCookies(authCookie, redirectCookie);
        MockHttpServletResponse response = new MockHttpServletResponse();

        repository.removeAuthorizationRequestCookies(request, response);

        Cookie deletedAuth = response.getCookie("oauth2_auth_request");
        Cookie deletedRedirect = response.getCookie("redirect_uri");
        // Both deleted: maxAge is 0
        assertNotNull(deletedAuth);
        assertEquals(0, deletedAuth.getMaxAge());
        assertNotNull(deletedRedirect);
        assertEquals(0, deletedRedirect.getMaxAge());
    }
}