package id.ac.ui.cs.advprog.yomu.service;

import id.ac.ui.cs.advprog.yomu.model.Question;
import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomu.repository.ReadingMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadingMaterialServiceTest {

    @Mock
    private ReadingMaterialRepository materialRepo;

    @Mock
    private QuizAttemptRepository attemptRepo;

    @InjectMocks
    private ReadingMaterialService readingMaterialService;

    private ReadingMaterial mathMaterial;
    private final String userId = "user-123";
    private final String materialId = "math-abc";

    @BeforeEach
    void setUp() {
        mathMaterial = new ReadingMaterial();
        mathMaterial.setId(materialId);

        Question q1 = new Question();
        q1.setCorrectOptionIndex(1); // Answer B

        Question q2 = new Question();
        q2.setCorrectOptionIndex(0); // Answer A

        mathMaterial.getQuestions().add(q1);
        mathMaterial.getQuestions().add(q2);
    }

    @Test
    void testSubmitQuiz_FullScore() {
        // Arrange
        when(attemptRepo.existsByUserIdAndMaterialId(userId, materialId)).thenReturn(false);
        when(materialRepo.findById(materialId)).thenReturn(mathMaterial);
        List<Integer> studentAnswers = Arrays.asList(1, 0);

        // Act
        double score = readingMaterialService.submitQuiz(userId, materialId, studentAnswers, 120);

        // Assert
        assertEquals(100.0, score);
        verify(attemptRepo, times(1)).save(any());
    }

    @Test
    void testSubmitQuiz_PartialScore() {
        // Arrange
        when(attemptRepo.existsByUserIdAndMaterialId(userId, materialId)).thenReturn(false);
        when(materialRepo.findById(materialId)).thenReturn(mathMaterial);
        List<Integer> studentAnswers = Arrays.asList(1, 1); // Only first answer is correct

        // Act
        double score = readingMaterialService.submitQuiz(userId, materialId, studentAnswers, 150);

        // Assert
        assertEquals(50.0, score);
    }

    @Test
    void testSubmitQuiz_OneShotRuleViolation() {
        // Arrange: Simulate that the user has already finished this quiz
        when(attemptRepo.existsByUserIdAndMaterialId(userId, materialId)).thenReturn(true);
        List<Integer> studentAnswers = Arrays.asList(1, 0);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            readingMaterialService.submitQuiz(userId, materialId, studentAnswers, 60);
        });

        // Ensure no score was calculated or saved
        verify(attemptRepo, never()).save(any());
    }

    @Test
    void testSubmitQuiz_InvalidAnswerCount() {
        // Arrange
        when(attemptRepo.existsByUserIdAndMaterialId(userId, materialId)).thenReturn(false);
        when(materialRepo.findById(materialId)).thenReturn(mathMaterial);
        List<Integer> studentAnswers = Arrays.asList(1); // Only 1 answer for 2 questions

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            readingMaterialService.submitQuiz(userId, materialId, studentAnswers, 30);
        });
    }

    @Test
    void testSubmitQuiz_MaterialNotFound() {
        // Arrange
        when(attemptRepo.existsByUserIdAndMaterialId(userId, materialId)).thenReturn(false);
        when(materialRepo.findById(materialId)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            readingMaterialService.submitQuiz(userId, materialId, Arrays.asList(1, 0), 45);
        });
    }
}