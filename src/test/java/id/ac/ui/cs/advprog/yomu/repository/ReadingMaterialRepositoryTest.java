package id.ac.ui.cs.advprog.yomu.repository;

import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class ReadingMaterialRepositoryTest {
    private ReadingMaterialRepository repository;
    private ReadingMaterial material;

    @BeforeEach
    void setUp() {
        repository = new ReadingMaterialRepository();
        material = new ReadingMaterial("Test Title", "Test Content", "Test Category");
    }

    @Test
    void testSaveAndFindAll() {
        repository.save(material);
        List<ReadingMaterial> materials = repository.findAll();

        assertEquals(1, materials.size());
        assertEquals(material.getId(), materials.get(0).getId());
    }

    @Test
    void testFindById() {
        repository.save(material);
        ReadingMaterial found = repository.findById(material.getId());

        assertNotNull(found);
        assertEquals(material.getTitle(), found.getTitle());
    }

    @Test
    void testDeleteById() {
        repository.save(material);
        repository.deleteById(material.getId());

        assertNull(repository.findById(material.getId()));
        assertTrue(repository.findAll().isEmpty());
    }
}