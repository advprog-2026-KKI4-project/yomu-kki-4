package id.ac.ui.cs.advprog.yomu.auth.config;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CookieUtilsTest {

    @Test
    void getCookie_shouldReturnCookie_whenNameMatches() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("jwt", "token-value");
        request.setCookies(cookie);

        Optional<Cookie> result = CookieUtils.getCookie(request, "jwt");

        assertTrue(result.isPresent());
        assertEquals("jwt", result.get().getName());
        assertEquals("token-value", result.get().getValue());
    }

    @Test
    void getCookie_shouldReturnEmpty_whenNameDoesNotMatch() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("other", "value"));

        Optional<Cookie> result = CookieUtils.getCookie(request, "jwt");

        assertFalse(result.isPresent());
    }

    @Test
    void getCookie_shouldReturnEmpty_whenNoCookies() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        Optional<Cookie> result = CookieUtils.getCookie(request, "jwt");

        assertFalse(result.isPresent());
    }

    @Test
    void getCookie_shouldReturnEmpty_whenCookiesArrayIsNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        // Don't set any cookies - MockHttpServletRequest returns null by default
        // for getCookies() when no cookies have been set, so just don't call setCookies

        Optional<Cookie> result = CookieUtils.getCookie(request, "jwt");

        assertFalse(result.isPresent());
    }

    @Test
    void addCookie_shouldSetCookieWithCorrectAttributes() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        CookieUtils.addCookie(response, "jwt", "token-value", 86400);

        Cookie cookie = response.getCookie("jwt");
        assertNotNull(cookie);
        assertEquals("jwt", cookie.getName());
        assertEquals("token-value", cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
        assertEquals(86400, cookie.getMaxAge());
    }

    @Test
    void deleteCookie_shouldClearCookieValueAndSetMaxAgeToZero() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie existingCookie = new Cookie("jwt", "token-value");
        request.setCookies(existingCookie);
        MockHttpServletResponse response = new MockHttpServletResponse();

        CookieUtils.deleteCookie(request, response, "jwt");

        Cookie deletedCookie = response.getCookie("jwt");
        assertNotNull(deletedCookie);
        assertEquals("", deletedCookie.getValue());
        assertEquals("/", deletedCookie.getPath());
        assertEquals(0, deletedCookie.getMaxAge());
    }

    @Test
    void deleteCookie_shouldDoNothing_whenCookieNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("other", "value"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        CookieUtils.deleteCookie(request, response, "jwt");

        assertNull(response.getCookie("jwt"));
    }

    @Test
    void serializeAndDeserialize_shouldRoundtripObject() {
        String original = "test-object-value";

        String serialized = CookieUtils.serialize(original);
        assertNotNull(serialized);

        Cookie cookie = new Cookie("data", serialized);
        String deserialized = CookieUtils.deserialize(cookie, String.class);

        assertEquals(original, deserialized);
    }

    @Test
    void getCookie_shouldMatchFirstOfMultipleCookies() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie other = new Cookie("other", "other-value");
        Cookie jwt = new Cookie("jwt", "token-value");
        request.setCookies(other, jwt);

        Optional<Cookie> result = CookieUtils.getCookie(request, "jwt");

        assertTrue(result.isPresent());
        assertEquals("token-value", result.get().getValue());
    }
}