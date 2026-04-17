package id.ac.ui.cs.advprog.yomu.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

public class ModelUsageTest {

    @Test
    public void testReadingMaterialUsage() {
        ReadingMaterial material = new ReadingMaterial();
        material.setTitle("Math");
        material.setContent("Content");
        material.setCategory("Education");

        List<Question> questions = Arrays.asList(new Question());
        material.setQuestions(questions);

        assertEquals("Math", material.getTitle());
        assertEquals("Content", material.getContent());
        assertEquals("Education", material.getCategory());
        assertEquals(1, material.getQuestions().size());
        assertNotNull(material.getId());
    }

    @Test
    public void testQuestionUsage() {
        Question question = new Question();
        question.setQuestionText("What is 1+1?");
        question.setOptions(Arrays.asList("1", "2"));
        question.setCorrectOptionIndex(1);

        assertEquals("What is 1+1?", question.getQuestionText());
        assertEquals(2, question.getOptions().size());
        assertEquals(1, question.getCorrectOptionIndex());
        assertNotNull(question.getId());
    }

    @Test
    public void testQuizAttemptUsage() {
        QuizAttempt attempt = new QuizAttempt("user1", "mat1", 100.0, 300);

        assertEquals("user1", attempt.getUserId());
        assertEquals("mat1", attempt.getMaterialId());
        assertEquals(100.0, attempt.getScore());
        assertEquals(300, attempt.getDuration());
        assertNotNull(attempt.getId());
        assertNotNull(attempt.getCompletedAt());
    }
}