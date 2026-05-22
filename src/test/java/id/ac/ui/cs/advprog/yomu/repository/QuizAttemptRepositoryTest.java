package id.ac.ui.cs.advprog.yomu.repository;

import id.ac.ui.cs.advprog.yomu.model.QuizAttempt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizAttemptRepositoryTest {

    private QuizAttemptRepository repository;

    @BeforeEach
    void setUp() {
        repository = new QuizAttemptRepository();
    }

    @Test
    void testSaveAndFindAll() {
        QuizAttempt attempt = new QuizAttempt("user1", "mat1", 85.0, 90, Arrays.asList(0, 1));
        repository.save(attempt);

        List<QuizAttempt> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("user1", all.get(0).getUserId());
        assertEquals("mat1", all.get(0).getMaterialId());
    }

    @Test
    void testSaveOverwritesExistingId() {
        QuizAttempt attempt = new QuizAttempt("user1", "mat1", 85.0, 90, Arrays.asList(0, 1));
        repository.save(attempt);

        QuizAttempt updated = new QuizAttempt("user1", "mat1", 95.0, 60, Arrays.asList(0, 1));
        updated.setId(attempt.getId()); // same ID
        repository.save(updated);

        List<QuizAttempt> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals(95.0, all.get(0).getScore());
        assertEquals(60, all.get(0).getDurationInSeconds());
    }

    @Test
    void testFindByUserIdAndMaterialIdFound() {
        QuizAttempt attempt = new QuizAttempt("user1", "mat1", 85.0, 90, Arrays.asList(0, 1));
        repository.save(attempt);

        QuizAttempt found = repository.findByUserIdAndMaterialId("user1", "mat1");
        assertNotNull(found);
        assertEquals(85.0, found.getScore());
    }

    @Test
    void testFindByUserIdAndMaterialIdNotFound() {
        QuizAttempt attempt = new QuizAttempt("user1", "mat1", 85.0, 90, Arrays.asList(0, 1));
        repository.save(attempt);

        assertNull(repository.findByUserIdAndMaterialId("user2", "mat1"));
        assertNull(repository.findByUserIdAndMaterialId("user1", "mat2"));
    }

    @Test
    void testExistsByUserIdAndMaterialId() {
        assertFalse(repository.existsByUserIdAndMaterialId("user1", "mat1"));

        QuizAttempt attempt = new QuizAttempt("user1", "mat1", 85.0, 90, Arrays.asList(0, 1));
        repository.save(attempt);

        assertTrue(repository.existsByUserIdAndMaterialId("user1", "mat1"));
        assertFalse(repository.existsByUserIdAndMaterialId("user1", "mat2"));
    }

    @Test
    void testDeleteByMaterialId() {
        QuizAttempt a1 = new QuizAttempt("user1", "mat1", 85.0, 90, Arrays.asList(0));
        QuizAttempt a2 = new QuizAttempt("user2", "mat1", 70.0, 120, Arrays.asList(1));
        QuizAttempt a3 = new QuizAttempt("user1", "mat2", 90.0, 60, Arrays.asList(0));
        repository.save(a1);
        repository.save(a2);
        repository.save(a3);

        repository.deleteByMaterialId("mat1");

        List<QuizAttempt> remaining = repository.findAll();
        assertEquals(1, remaining.size());
        assertEquals("mat2", remaining.get(0).getMaterialId());
    }

    @Test
    void testFindAllReturnsEmptyListWhenNothingSaved() {
        List<QuizAttempt> all = repository.findAll();
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }
}
