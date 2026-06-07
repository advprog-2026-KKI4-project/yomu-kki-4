package id.ac.ui.cs.advprog.yomu.learningandquiz.controller;

import id.ac.ui.cs.advprog.yomu.learningandquiz.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.learningandquiz.service.ReadingMaterialService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuizSubmissionControllerTest {

    private ReadingMaterialController controller;
    private ReadingMaterialService service;

    @BeforeEach
    void setUp() {
        service = mock(ReadingMaterialService.class);
        controller = new ReadingMaterialController(service);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "student@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testSubmitQuizExecutionSuccess() {
        ReadingMaterial mockMaterial = mock(ReadingMaterial.class);
        when(mockMaterial.getQuestions()).thenReturn(List.of());
        when(mockMaterial.getTimeLimit()).thenReturn(300);
        when(service.getById("math-123")).thenReturn(mockMaterial);

        when(service.submitQuiz(anyString(), anyString(), anyList(), anyLong()))
                .thenReturn(100.0);

        Map<String, String> params = new HashMap<>();
        params.put("answers[0]", "1");
        params.put("answers[1]", "0");

        String view = controller.submitQuiz("math-123", 45L, params);

        assertNotNull(view);
        assertTrue(view.contains("redirect:/quiz/result"));
        verify(service, times(1)).submitQuiz("student@test.com", "math-123", List.of(1, 0), 45L);
    }
}