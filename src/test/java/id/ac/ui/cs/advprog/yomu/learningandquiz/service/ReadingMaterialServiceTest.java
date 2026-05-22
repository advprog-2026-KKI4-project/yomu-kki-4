package id.ac.ui.cs.advprog.yomu.learningandquiz.service;

import id.ac.ui.cs.advprog.yomu.learningandquiz.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.learningandquiz.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomu.learningandquiz.repository.ReadingMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReadingMaterialServiceTest {

    private ReadingMaterialService service;
    private ReadingMaterialRepository materialRepo;
    private QuizAttemptRepository attemptRepo;
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        materialRepo = mock(ReadingMaterialRepository.class);
        attemptRepo = mock(QuizAttemptRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        service = new ReadingMaterialService(materialRepo, attemptRepo, eventPublisher);
    }

    @Test
    void testSubmitQuizOneShotRuleViolation() {
        String userId = "student-123";
        String materialId = "math-01";
        List<Integer> answers = List.of(1, 0);

        when(attemptRepo.existsByUserIdAndMaterialId(userId, materialId)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            service.submitQuiz(userId, materialId, answers, 30L);
        });

        verify(materialRepo, never()).findById(anyString());
    }

    @Test
    void testSubmitQuizMaterialNotFound() {
        String userId = "student-123";
        String materialId = "ghost-id";
        List<Integer> answers = List.of(1, 0);

        when(attemptRepo.existsByUserIdAndMaterialId(userId, materialId)).thenReturn(false);
        when(materialRepo.findById(materialId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            service.submitQuiz(userId, materialId, answers, 30L);
        });
    }

    @Test
    void testGetMaterialByIdSuccess() {
        String materialId = "bio-01";
        ReadingMaterial material = new ReadingMaterial();
        material.setId(materialId);

        when(materialRepo.findById(materialId)).thenReturn(material);

        ReadingMaterial result = service.getById(materialId);

        assertNotNull(result);
        assertEquals(materialId, result.getId());
    }
}