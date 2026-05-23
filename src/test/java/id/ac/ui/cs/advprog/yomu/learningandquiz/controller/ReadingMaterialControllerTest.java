package id.ac.ui.cs.advprog.yomu.learningandquiz.controller;

import id.ac.ui.cs.advprog.yomu.learningandquiz.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.learningandquiz.service.ReadingMaterialService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReadingMaterialControllerTest {

    private ReadingMaterialController controller;
    private ReadingMaterialService service;

    @BeforeEach
    void setUp() {
        service = mock(ReadingMaterialService.class);
        controller = new ReadingMaterialController(service);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "admin@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testSubmitQuizExtractsAnswersAndRedirects() {
        ReadingMaterial material = new ReadingMaterial();
        material.setTitle("Test");
        material.setTimeLimit(300);
        when(service.getById("test-id")).thenReturn(material);
        when(service.submitQuiz(eq("admin@test.com"), eq("test-id"), anyList(), eq(60L)))
                .thenReturn(100.0);

        Map<String, String> params = new HashMap<>();
        params.put("answers[0]", "1");
        params.put("answers[1]", "0");
        params.put("userId", "should-be-ignored");
        params.put("duration", "60");

        String result = controller.submitQuiz("test-id", 60L, params);

        assertTrue(result.startsWith("redirect:/quiz/result?"));
        assertTrue(result.contains("score=100.0"));
    }

    @Test
    void testSubmitQuizSortsAnswersNumerically() {
        ReadingMaterial material = new ReadingMaterial();
        material.setTimeLimit(300);
        when(service.getById("test-id")).thenReturn(material);
        when(service.submitQuiz(anyString(), anyString(), anyList(), anyLong()))
                .thenReturn(80.0);

        Map<String, String> params = new HashMap<>();
        params.put("answers[10]", "3");
        params.put("answers[2]", "1");
        params.put("duration", "30");

        String result = controller.submitQuiz("test-id", 30L, params);

        assertTrue(result.startsWith("redirect:/quiz/result?"));
        verify(service).submitQuiz(eq("admin@test.com"), eq("test-id"), eq(List.of(1, 3)), eq(30L));
    }

    @Test
    void testAddMaterialDelegatesToService() {
        ReadingMaterial material = new ReadingMaterial();
        String result = controller.add(material);

        assertEquals("redirect:/reading", result);
        verify(service, times(1)).add(material);
    }

    @Test
    void testUpdateMaterialPreservesQuestions() {
        ReadingMaterial existing = new ReadingMaterial();
        existing.setTitle("Old Title");
        when(service.getById("test-id")).thenReturn(existing);

        ReadingMaterial updated = new ReadingMaterial();
        updated.setTitle("New Title");
        updated.setCategory("Code");
        updated.setContent("New Content");
        updated.setTimeLimit(60);

        String result = controller.update("test-id", updated);

        assertEquals("redirect:/reading", result);
        assertEquals("New Title", existing.getTitle());
        assertEquals("Code", existing.getCategory());
        assertEquals("New Content", existing.getContent());
        assertEquals(60, existing.getTimeLimit());
        verify(service).add(existing);
    }

    @Test
    void testDeleteMaterialDelegatesToService() {
        String result = controller.delete("test-id");

        assertEquals("redirect:/reading", result);
        verify(service, times(1)).delete("test-id");
    }

    @Test
    void testSubmitQuizHandlesServiceExceptionGracefully() {
        when(service.getById("test-id")).thenReturn(new ReadingMaterial());
        when(service.submitQuiz(anyString(), anyString(), anyList(), anyLong()))
                .thenThrow(new IllegalArgumentException("Material not found."));

        Map<String, String> params = Collections.emptyMap();
        String result = controller.submitQuiz("test-id", 30L, params);

        assertTrue(result.startsWith("redirect:/reading?error="));
    }
}