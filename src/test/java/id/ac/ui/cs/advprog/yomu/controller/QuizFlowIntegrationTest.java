package id.ac.ui.cs.advprog.yomu.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuizFlowIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void testReadingPageRequiresAuthentication() {
        // RestTemplate follows redirects, so unauthenticated /reading → /login (200)
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl() + "/reading", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Login"),
                "Should redirect to login page for unauthenticated requests");
    }

    @Test
    void testMyLearningRequiresAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl() + "/my-learning", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Login"),
                "Should redirect to login page for unauthenticated requests");
    }

    @Test
    void testQuizResultPageRequiresAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl() + "/quiz/result?score=80&duration=60&baseScore=75&bonus=5&remaining=0",
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Login"),
                "Should redirect to login page for unauthenticated requests");
    }

    @Test
    void testStaticCssIsAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl() + "/css/dashboard.css", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
