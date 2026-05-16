package id.ac.ui.cs.advprog.yomu.repository;

import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
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
        mat.setTitle("Test Material");
        repository.save(mat);

        List<ReadingMaterial> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("Test Material", all.get(0).getTitle());
    }

    @Test
    void testFindById() {
        ReadingMaterial mat = new ReadingMaterial();
        String id = mat.getId();
        repository.save(mat);

        ReadingMaterial found = repository.findById(id);
        assertNotNull(found);
        assertEquals(id, found.getId());
    }

    @Test
    void testDelete() {
        ReadingMaterial mat = new ReadingMaterial();
        String id = mat.getId();
        repository.save(mat);

        repository.deleteById(id);
        assertNull(repository.findById(id));
    }
}