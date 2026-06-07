package id.ac.ui.cs.advprog.yomu.learningandquiz.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelUsageTest {

    @Test
    void testQuestionGettersAndSetters() {
        Question question = new Question();
        assertNotNull(question.getId());
        assertNotNull(question.getOptions());

        question.setId("q-1");
        question.setQuestionText("What is 1+1?");
        List<String> options = Arrays.asList("1", "2");
        question.setOptions(options);
        question.setCorrectOptionIndex(1);

        assertEquals("q-1", question.getId());
        assertEquals("What is 1+1?", question.getQuestionText());
        assertEquals(options, question.getOptions());
        assertEquals(1, question.getCorrectOptionIndex());
    }

    @Test
    void testQuizAttemptGettersAndSetters() {
        QuizAttempt attempt = new QuizAttempt();
        assertNotNull(attempt.getId());
        assertNotNull(attempt.getCompletedAt());

        LocalDateTime now = LocalDateTime.now();
        List<Integer> answers = Arrays.asList(1, 2);

        attempt.setId("attempt-1");
        attempt.setUserId("user-1");
        attempt.setMaterialId("mat-1");
        attempt.setScore(95.5);
        attempt.setDurationInSeconds(120L);
        attempt.setCompletedAt(now);
        attempt.setAnswers(answers);

        assertEquals("attempt-1", attempt.getId());
        assertEquals("user-1", attempt.getUserId());
        assertEquals("mat-1", attempt.getMaterialId());
        assertEquals(95.5, attempt.getScore());
        assertEquals(120L, attempt.getDurationInSeconds());
        assertEquals(now, attempt.getCompletedAt());
        assertEquals(answers, attempt.getAnswers());
    }

    @Test
    void testQuizAttemptAllArgsConstructor() {
        List<Integer> answers = Arrays.asList(0, 1);
        QuizAttempt attempt = new QuizAttempt("user-2", "mat-2", 80.0, 300L, answers);

        assertNotNull(attempt.getId());
        assertNotNull(attempt.getCompletedAt());
        assertEquals("user-2", attempt.getUserId());
        assertEquals("mat-2", attempt.getMaterialId());
        assertEquals(80.0, attempt.getScore());
        assertEquals(300L, attempt.getDurationInSeconds());
        assertEquals(answers, attempt.getAnswers());
    }

    @Test
    void testReadingMaterialGettersAndSetters() {
        ReadingMaterial material = new ReadingMaterial();
        assertNotNull(material.getQuestions());

        material.setId("mat-1");
        material.setTitle("Spring Boot Intro");
        material.setCategory("Programming");
        material.setContent("Content goes here");
        material.setTimeLimit(60);
        material.setProgress(50);

        List<Question> questions = new ArrayList<>();
        Question q1 = new Question();
        questions.add(q1);
        material.setQuestions(questions);

        assertEquals("mat-1", material.getId());
        assertEquals("Spring Boot Intro", material.getTitle());
        assertEquals("Programming", material.getCategory());
        assertEquals("Content goes here", material.getContent());
        assertEquals(60, material.getTimeLimit());
        assertEquals(50, material.getProgress());
        assertEquals(questions, material.getQuestions());

        Question q2 = new Question();
        material.addQuestion(q2);
        assertEquals(2, material.getQuestions().size());
        assertTrue(material.getQuestions().contains(q2));
    }
}