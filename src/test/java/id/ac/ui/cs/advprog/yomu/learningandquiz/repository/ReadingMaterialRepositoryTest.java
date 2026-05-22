package id.ac.ui.cs.advprog.yomu.learningandquiz.repository;

import id.ac.ui.cs.advprog.yomu.learningandquiz.model.ReadingMaterial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class ReadingMaterialRepositoryTest {
    private ReadingMaterialRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ReadingMaterialRepository();
    }

    @Test
    void testSaveAndFindAll() {
        ReadingMaterial mat = new ReadingMaterial();
        mat.setId("mat-test-1");
        mat.setTitle("Test Material");
        repository.save(mat);

        List<ReadingMaterial> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("Test Material", all.get(0).getTitle());
    }

    @Test
    void testFindById() {
        ReadingMaterial mat = new ReadingMaterial();
        mat.setId("mat-test-2");
        repository.save(mat);

        ReadingMaterial found = repository.findById("mat-test-2");
        assertNotNull(found);
        assertEquals("mat-test-2", found.getId());
    }

    @Test
    void testDelete() {
        ReadingMaterial mat = new ReadingMaterial();
        mat.setId("mat-test-3");
        repository.save(mat);

        repository.deleteById("mat-test-3");
        assertNull(repository.findById("mat-test-3"));
    }
}