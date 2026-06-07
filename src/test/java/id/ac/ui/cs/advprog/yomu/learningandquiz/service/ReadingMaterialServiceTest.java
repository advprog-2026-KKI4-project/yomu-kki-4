package id.ac.ui.cs.advprog.yomu.learningandquiz.service;

import id.ac.ui.cs.advprog.yomu.achievement.event.QuizCompletedEvent;
import id.ac.ui.cs.advprog.yomu.achievement.event.ReadingCompletedEvent;
import id.ac.ui.cs.advprog.yomu.learningandquiz.model.Question;
import id.ac.ui.cs.advprog.yomu.learningandquiz.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.learningandquiz.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomu.learningandquiz.repository.ReadingMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
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
    void testAddMaterial() {
        ReadingMaterial material = new ReadingMaterial();
        when(materialRepo.save(material)).thenReturn(material);

        ReadingMaterial result = service.add(material);

        assertNotNull(result);
        verify(materialRepo, times(1)).save(material);
    }

    @Test
    void testGetAllMaterials() {
        ReadingMaterial m1 = new ReadingMaterial();
        when(materialRepo.findAll()).thenReturn(List.of(m1));

        List<ReadingMaterial> result = service.getAll();

        assertEquals(1, result.size());
        verify(materialRepo, times(1)).findAll();
    }

    @Test
    void testGetId() {
        String id = "mat-1";
        ReadingMaterial material = new ReadingMaterial();
        when(materialRepo.findById(id)).thenReturn(material);

        ReadingMaterial result = service.getById(id);

        assertNotNull(result);
        verify(materialRepo, times(1)).findById(id);
    }

    @Test
    void testDeleteMaterial() {
        String id = "mat-1";
        doNothing().when(materialRepo).deleteById(id);
        doNothing().when(attemptRepo).deleteByMaterialId(id);

        service.delete(id);

        verify(materialRepo, times(1)).deleteById(id);
        verify(attemptRepo, times(1)).deleteByMaterialId(id);
    }

    @Test
    void testCompleteReadingMaterialFound() {
        String userId = "user-1";
        String id = "mat-1";
        ReadingMaterial material = new ReadingMaterial();

        when(materialRepo.findById(id)).thenReturn(material);
        when(materialRepo.save(material)).thenReturn(material);

        service.completeReading(userId, id);

        assertEquals(50, material.getProgress());
        verify(materialRepo, times(1)).save(material);
        verify(eventPublisher, times(1)).publishEvent(any(ReadingCompletedEvent.class));
    }

    @Test
    void testCompleteReadingMaterialNotFound() {
        String userId = "user-1";
        String id = "mat-1";

        when(materialRepo.findById(id)).thenReturn(null);

        service.completeReading(userId, id);

        verify(materialRepo, never()).save(any());
        verify(eventPublisher, times(1)).publishEvent(any(ReadingCompletedEvent.class));
    }

    @Test
    void testSubmitQuizAlreadyAttempted() {
        String userId = "user-1";
        String id = "mat-1";
        List<Integer> answers = List.of(1);

        when(attemptRepo.existsByUserIdAndMaterialId(userId, id)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            service.submitQuiz(userId, id, answers, 10L);
        });
    }

    @Test
    void testSubmitQuizMaterialNotFound() {
        String userId = "user-1";
        String id = "mat-1";
        List<Integer> answers = List.of(1);

        when(attemptRepo.existsByUserIdAndMaterialId(userId, id)).thenReturn(false);
        when(materialRepo.findById(id)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            service.submitQuiz(userId, id, answers, 10L);
        });
    }

    @Test
    void testSubmitQuizNoQuestions() {
        String userId = "user-1";
        String id = "mat-1";
        List<Integer> answers = List.of(1);
        ReadingMaterial material = new ReadingMaterial();
        material.setQuestions(new ArrayList<>());

        when(attemptRepo.existsByUserIdAndMaterialId(userId, id)).thenReturn(false);
        when(materialRepo.findById(id)).thenReturn(material);

        assertThrows(IllegalStateException.class, () -> {
            service.submitQuiz(userId, id, answers, 10L);
        });
    }

    @Test
    void testSubmitQuizAnswerCountMismatch() {
        String userId = "user-1";
        String id = "mat-1";
        List<Integer> answers = List.of(1, 2);

        Question q1 = new Question();
        ReadingMaterial material = new ReadingMaterial();
        material.setQuestions(new ArrayList<>(List.of(q1)));

        when(attemptRepo.existsByUserIdAndMaterialId(userId, id)).thenReturn(false);
        when(materialRepo.findById(id)).thenReturn(material);

        assertThrows(IllegalArgumentException.class, () -> {
            service.submitQuiz(userId, id, answers, 10L);
        });
    }

    @Test
    void testSubmitQuizInvalidTimeLimit() {
        String userId = "user-1";
        String id = "mat-1";
        List<Integer> answers = List.of(1);

        Question q1 = new Question();
        ReadingMaterial material = new ReadingMaterial();
        material.setTimeLimit(0);
        material.setQuestions(new ArrayList<>(List.of(q1)));

        when(attemptRepo.existsByUserIdAndMaterialId(userId, id)).thenReturn(false);
        when(materialRepo.findById(id)).thenReturn(material);

        assertThrows(IllegalStateException.class, () -> {
            service.submitQuiz(userId, id, answers, 10L);
        });
    }

    @Test
    void testSubmitQuizSuccessCalculatesBonusScore() {
        String userId = "user-1";
        String id = "mat-1";
        List<Integer> answers = List.of(1, 2);

        Question q1 = new Question();
        q1.setCorrectOptionIndex(1);
        Question q2 = new Question();
        q2.setCorrectOptionIndex(2);

        ReadingMaterial material = new ReadingMaterial();
        material.setTimeLimit(60);
        material.setQuestions(new ArrayList<>(List.of(q1, q2)));

        when(attemptRepo.existsByUserIdAndMaterialId(anyString(), anyString())).thenReturn(false);
        when(materialRepo.findById(anyString())).thenReturn(material);
        when(materialRepo.save(any())).thenReturn(material);
        when(attemptRepo.save(any())).thenReturn(null);

        double finalScore = service.submitQuiz(userId, id, answers, 10L);

        assertTrue(finalScore > 0);
        assertEquals(100, material.getProgress());
        verify(materialRepo, times(1)).save(any());
        verify(attemptRepo, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any(QuizCompletedEvent.class));
    }
}