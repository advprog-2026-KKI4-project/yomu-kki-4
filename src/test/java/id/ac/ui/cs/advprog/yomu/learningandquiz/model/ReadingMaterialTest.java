package id.ac.ui.cs.advprog.yomu.learningandquiz.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

class ReadingMaterialTest {
    private ReadingMaterial material;

    @BeforeEach
    void setUp() {
        material = new ReadingMaterial();
    }

    @Test
    void testReadingMaterialProperties() {
        material.setId("test-uuid-123");
        material.setTitle("Quadratic Equations");
        material.setContent("Standard form is ax^2 + bx + c = 0");
        material.setCategory("Math");

        assertEquals("test-uuid-123", material.getId());
        assertEquals("Quadratic Equations", material.getTitle());
        assertEquals("Standard form is ax^2 + bx + c = 0", material.getContent());
        assertEquals("Math", material.getCategory());
    }

    @Test
    void testQuestionsRelationship() {
        List<Question> questions = new ArrayList<>();
        Question q = new Question();
        q.setQuestionText("What is 2^2?");
        questions.add(q);

        material.setQuestions(questions);
        assertEquals(1, material.getQuestions().size());
        assertEquals("What is 2^2?", material.getQuestions().get(0).getQuestionText());
    }
}